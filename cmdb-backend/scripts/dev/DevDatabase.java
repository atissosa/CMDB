import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.security.MessageDigest;

/** Development database lifecycle only. Never used by production code or automated tests. */
class DevDatabase {
    static Path privateDir=Path.of(System.getenv("CMDB_DEV_CONFIG_DIR"));
    static String secret(String name) throws Exception {return Files.readString(privateDir.resolve(name)).strip();}
    static Connection connect(String database, boolean admin) throws Exception {
        var props=new Properties();props.setProperty("user",admin?"sa":"cmdb_dev_app");
        props.setProperty("password",secret(admin?"sa-password":"db-password"));
        return DriverManager.getConnection("jdbc:sqlserver://127.0.0.1:"+System.getenv("CMDB_DEV_SQL_PORT")
                +";databaseName="+database+";encrypt=false;loginTimeout=5",props);
    }
    static void sql(Connection c,String text) throws SQLException {try(var s=c.createStatement()){s.setQueryTimeout(120);s.execute(text);}}
    static String scalar(Connection c,String text) throws SQLException {try(var s=c.createStatement();var r=s.executeQuery(text)){if(!r.next())throw new SQLException("Missing row");return r.getString(1);}}
    static String hash(Path p) throws Exception {return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(p)));}
    static String literal(String text){return "N'"+text.replace("'","''")+"'";}
    static void initialize() throws Exception {
        Path schema=Path.of("infra/dev/sql/01-schema.sql");
        String fingerprint=hash(schema)+":"+hash(Path.of("infra/dev/sql/02-seed.sql"));
        boolean created;
        try(var c=connect("master",true)) {
            created=scalar(c,"SELECT IIF(DB_ID('cmdb_dev') IS NULL,1,0)").equals("1");
            if(created)sql(c,"CREATE DATABASE [cmdb_dev]");
        }
        try(var c=connect("cmdb_dev",true)) {
            if(!created) {
                if(!scalar(c,"SELECT IIF(OBJECT_ID('dbo.CmdbDevInitialization','U') IS NOT NULL,1,0)").equals("1"))
                    throw new IllegalStateException("Existing cmdb_dev is unrecognized or initialization incomplete; inspect manually. No data changed.");
                if(!scalar(c,"SELECT SchemaHash FROM dbo.CmdbDevInitialization WHERE Id=1").equals(fingerprint))
                    throw new IllegalStateException("Existing development schema differs; manual migration required. No data changed.");
                System.out.println("{\"initialization\":\"existing_preserved\"}");return;
            }
            c.setAutoCommit(false);
            try {
                for(String file:List.of("infra/dev/sql/01-schema.sql","infra/dev/sql/02-seed.sql")) {
                    for(String batch:Files.readString(Path.of(file)).split("(?im)^[\\t ]*GO[\\t ]*\\r?$"))if(!batch.isBlank())sql(c,batch);
                }
                try(var s=c.prepareStatement("INSERT dbo.Usuarios (Usuario,Contrasena,IdRol,IdDepartamento,Activo) SELECT ?,?,r.Id,d.Id,1 FROM dbo.Roles r CROSS JOIN dbo.Departamentos d WHERE r.Nombre='Administrador'")) {
                    s.setString(1,"devadmin");s.setString(2,secret("app-password"));if(s.executeUpdate()!=1)throw new IllegalStateException("Synthetic account not initialized");
                }
                sql(c,"CREATE TABLE dbo.CmdbDevInitialization (Id int PRIMARY KEY CHECK(Id=1),SchemaHash varchar(129) NOT NULL,CreatedAt datetime2 NOT NULL DEFAULT SYSUTCDATETIME())");
                try(var s=c.prepareStatement("INSERT dbo.CmdbDevInitialization (Id,SchemaHash) VALUES (1,?)")){s.setString(1,fingerprint);s.executeUpdate();}
            sql(c,"CREATE LOGIN [cmdb_dev_app] WITH PASSWORD="+literal(secret("db-password"))+", CHECK_POLICY=ON, DEFAULT_DATABASE=[cmdb_dev]");
            sql(c,"CREATE USER [cmdb_dev_app] FOR LOGIN [cmdb_dev_app]");
            sql(c,"ALTER ROLE db_datareader ADD MEMBER [cmdb_dev_app]");
            sql(c,"ALTER ROLE db_datawriter ADD MEMBER [cmdb_dev_app]");
            sql(c,"GRANT EXECUTE ON SCHEMA::dbo TO [cmdb_dev_app]");
                c.commit();
            } catch(Exception e){c.rollback();throw e;} finally {c.setAutoCommit(true);}

        }
        System.out.println("{\"initialization\":\"new_synthetic_database\"}");
    }
    static void inspect() throws Exception {
        try(var c=connect("cmdb_dev",false)) {
            String tables=scalar(c,"SELECT COUNT(*) FROM sys.tables WHERE is_ms_shipped=0");
            String views=scalar(c,"SELECT COUNT(*) FROM sys.views WHERE is_ms_shipped=0");
            String procedures=scalar(c,"SELECT COUNT(*) FROM sys.procedures WHERE is_ms_shipped=0");
            String admin=scalar(c,"SELECT IS_SRVROLEMEMBER('sysadmin')");
            if(!admin.equals("0"))throw new IllegalStateException("Application login must not be sysadmin");
            System.out.printf("{\"database\":\"cmdb_dev\",\"tables\":%s,\"views\":%s,\"procedures\":%s,\"app_sysadmin\":false,\"engine\":\"%s\"}%n",tables,views,procedures,scalar(c,"SELECT CAST(SERVERPROPERTY('ProductVersion') AS varchar(40))"));
        }
    }
    public static void main(String[] args) throws Exception {
        try {
            switch(args[0]) {
                case "init" -> initialize();
                case "inspect" -> inspect();
                case "snapshot" -> {try(var c=connect("cmdb_dev",false)){System.out.println("{\"places\":"+scalar(c,"SELECT COUNT(*) FROM dbo.Lugares")+",\"users\":"+scalar(c,"SELECT COUNT(*) FROM dbo.Usuarios")+",\"equipment\":"+scalar(c,"SELECT COUNT(*) FROM dbo.Equipos")+",\"schema\":\""+scalar(c,"SELECT SchemaHash FROM dbo.CmdbDevInitialization WHERE Id=1")+"\"}");}}
                case "marker-remove" -> {try(var c=connect("cmdb_dev",false)){c.setAutoCommit(false);sql(c,"DELETE dbo.Departamentos WHERE Id IN (SELECT Id FROM dbo.Lugares WHERE Nombre='CMDB24-PERSISTENCE-MARKER')");sql(c,"DELETE dbo.Lugares WHERE Nombre='CMDB24-PERSISTENCE-MARKER'");c.commit();System.out.println("{\"marker_removed\":true}");}}
                default -> throw new IllegalArgumentException("Unknown database operation");
            }
        } catch(SQLException e){System.err.println("Database operation failed; SQL state="+e.getSQLState()+", code="+e.getErrorCode()+". Sensitive server messages omitted.");System.exit(1);}
    }
}
