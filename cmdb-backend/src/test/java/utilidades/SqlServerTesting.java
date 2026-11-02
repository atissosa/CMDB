package utilidades;

import com.atissosa.cmdb_backend.global.Propiedades;
import com.atissosa.cmdb_backend.config.ConfiguracionBackend;
import org.springframework.test.context.DynamicPropertyRegistry;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;
import org.testcontainers.mssqlserver.MSSQLServerContainer;

/** One disposable SQL Server per Maven test JVM, never a production instance. */
public final class SqlServerTesting {
    private static final String IMAGE = "mcr.microsoft.com/mssql/server:2022-CU20-ubuntu-22.04";

    private SqlServerTesting() { }

    private static final class Holder {
        private static final MSSQLServerContainer CONTAINER = start();
    }

    public static ConfiguracionBackend configuracion() {
        MSSQLServerContainer db = Holder.CONTAINER;
        return new ConfiguracionBackend(
                new ConfiguracionBackend.Bd(Propiedades.ProveedoresBD.MSSQLSERVER,
                        db.getHost(), db.getMappedPort(1433), "CMDB", db.getUsername(),
                db.getPassword(), false, false,
                new ConfiguracionBackend.Pool(1, 3, 3000L, 10000L, 120000L, 1500L)),
                new ConfiguracionBackend.Login(Propiedades.TiposLogin.CLASICO),
            new ConfiguracionBackend.Departamento("Mesa de Ayuda"),
            new ConfiguracionBackend.Cors(
                java.util.List.of("http://localhost:8080"),
                java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"),
                java.util.List.of("origin", "content-type", "accept", "authorization"),
                false,
                600));
    }

    public static void configurar() {
        Propiedades.configurar(configuracion());
    }

    public static void registrarPropiedades(DynamicPropertyRegistry registry) {
        var config = configuracion();
        registry.add("cmdb.bd.proveedor", () -> config.bd().proveedor().name());
        registry.add("cmdb.bd.servidor", () -> config.bd().servidor());
        registry.add("cmdb.bd.puerto", () -> config.bd().puerto());
        registry.add("cmdb.bd.nombre", () -> config.bd().nombre());
        registry.add("CMDB_DB_USERNAME", () -> config.bd().usuario());
        registry.add("CMDB_DB_PASSWORD", () -> config.bd().contrasenia());
        registry.add("cmdb.bd.encrypt", () -> config.bd().encrypt());
        registry.add("cmdb.bd.trust-server-certificate", () -> config.bd().trustServerCertificate());
        registry.add("cmdb.bd.pool.min-idle", () -> config.bd().pool().minIdle());
        registry.add("cmdb.bd.pool.max-size", () -> config.bd().pool().maxSize());
        registry.add("cmdb.bd.pool.connection-timeout-ms", () -> config.bd().pool().connectionTimeoutMs());
        registry.add("cmdb.bd.pool.idle-timeout-ms", () -> config.bd().pool().idleTimeoutMs());
        registry.add("cmdb.bd.pool.max-lifetime-ms", () -> config.bd().pool().maxLifetimeMs());
        registry.add("cmdb.bd.pool.validation-timeout-ms", () -> config.bd().pool().validationTimeoutMs());
        registry.add("cmdb.login.tipo", () -> config.login().tipo().name());
        registry.add("cmdb.departamento.defecto", () -> config.departamento().defecto());
        registry.add("cmdb.cors.allowed-origins", () -> String.join(",", config.cors().allowedOrigins()));
        registry.add("cmdb.cors.allowed-methods", () -> String.join(",", config.cors().allowedMethods()));
        registry.add("cmdb.cors.allowed-headers", () -> String.join(",", config.cors().allowedHeaders()));
        registry.add("cmdb.cors.allow-credentials", () -> config.cors().allowCredentials());
        registry.add("cmdb.cors.max-age-seconds", () -> config.cors().maxAgeSeconds());
    }

    private static MSSQLServerContainer start() {
        MSSQLServerContainer db = new MSSQLServerContainer(IMAGE)
                .acceptLicense()
                .withPassword("Test!" + UUID.randomUUID())
                .withEnv("MSSQL_PID", "Developer")
                .withEnv("MSSQL_MEMORY_LIMIT_MB", "2048")
                .withStartupTimeout(Duration.ofMinutes(3));
        db.start();
        Runtime.getRuntime().addShutdownHook(new Thread(db::stop));
        try (Connection connection = DriverManager.getConnection(db.getJdbcUrl(), db.getUsername(), db.getPassword())) {
            executeScript(connection, "db/legacy/01_estructuras.sql");
            executeScript(connection, "db/legacy/02_datos.sql");
            executeScript(connection, "db/legacy/03_precondiciones_tests.sql");
            return db;
        } catch (SQLException | IOException ex) {
            db.stop();
            throw new IllegalStateException("No se pudo inicializar la BD aislada con los fixtures legacy", ex);
        }
    }

    private static void executeScript(Connection connection, String resource) throws IOException, SQLException {
        String script;
        try (var input = SqlServerTesting.class.getClassLoader().getResourceAsStream(resource)) {
            if (input == null) {
                throw new IOException("Falta el fixture " + resource);
            }
            script = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        int batchNumber = 0;
        for (String batch : script.split("(?im)^[\\t ]*GO[\\t ]*\\r?$")) {
            batchNumber++;
            if (!batch.isBlank()) {
                try (var statement = connection.createStatement()) {
                    statement.setQueryTimeout(120);
                    statement.execute(batch);
                } catch (SQLException ex) {
                    throw new SQLException("Error en " + resource + ", lote " + batchNumber, ex);
                }
            }
        }
    }
}
