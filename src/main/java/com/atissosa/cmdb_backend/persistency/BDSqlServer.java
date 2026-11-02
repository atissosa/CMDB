package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import com.atissosa.cmdb_backend.global.Propiedades.ProveedoresBD;
import java.sql.SQLException;

public class BDSqlServer extends BD {

    protected BDSqlServer(String servidor, String puerto, String bd, String usuario, String contrasena) {
        super(servidor, puerto, bd, usuario, contrasena);
    }
    
    @Override
    protected ProveedoresBD getProveedor() {
        return ProveedoresBD.MSSQLSERVER;
    }
    
    @Override
    protected String getDriver() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }
    
    @Override
    protected String getConnectionString() {
        return "jdbc:sqlserver://" + this.getServidor() + ":" + this.getPuerto() + ";databaseName=" + this.getBd() + ";";
    }
    
    @Override
    protected void conectar(boolean transaccional) throws ExcepcionConexion {
        try {
            this.setConexion(dataSourceConfigurado().getConnection());
            this.setTransaccional(transaccional);
            this.getConexion().setAutoCommit(!transaccional);
        }
        catch (SQLException e) {
            this.desconectar();
            throw new ExcepcionConexion("No se pudo obtener una conexión SQL Server desde el DataSource.");
        }
    }
    
}
