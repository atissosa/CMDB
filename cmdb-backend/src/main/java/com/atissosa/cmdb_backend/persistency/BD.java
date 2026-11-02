package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import com.atissosa.cmdb_backend.global.Propiedades;
import com.atissosa.cmdb_backend.global.Propiedades.ProveedoresBD;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class BD {
    private static volatile DataSource dataSource;

    private String servidor;
    private String bd;
    private String puerto;
    private String usuario;
    private String contrasena;
    private Connection conexion;
    private boolean transaccional;

    protected BD(String servidor, String puerto, String bd, String usuario, String contrasena) {
        this.servidor = servidor;
        this.puerto = puerto;
        this.bd = bd;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    protected String getServidor() {
        return this.servidor;
    }

    protected String getBd() {
        return this.bd;
    }

    protected String getPuerto() {
        return this.puerto;
    }

    protected String getUsuario() {
        return this.usuario;
    }

    protected String getContrasena() {
        return this.contrasena;
    }

    protected Connection getConexion() {
        return this.conexion;
    }

    protected void setConexion(Connection conexion) {
        this.conexion = conexion;
    }

    protected boolean getTransaccional() {
        return this.transaccional;
    }

    protected void setTransaccional(boolean transaccional) {
        this.transaccional = transaccional;
    }

    protected abstract ProveedoresBD getProveedor();

    protected abstract String getDriver();

    protected abstract String getConnectionString();

    protected static BD nuevaConexion() {
        BD conexion = null;
        switch (Propiedades.BD_PROVEEDOR) {
            case MSSQLSERVER:
                conexion = new BDSqlServer(Propiedades.BD_SERVIDOR, Propiedades.BD_PUERTO, Propiedades.BD_BD, Propiedades.BD_USUARIO, Propiedades.BD_CONTRASENIA);
                break;
        }
        return conexion;
    }

    public static synchronized void configurarDataSource(DataSource nuevoDataSource) {
        dataSource = nuevoDataSource;
    }

    public static boolean tieneDataSourceConfigurado() {
        return dataSource != null;
    }

    protected static DataSource dataSourceConfigurado() throws ExcepcionConexion {
        if (dataSource == null) {
            throw new ExcepcionConexion("No se configuró un DataSource para la persistencia.");
        }
        return dataSource;
    }

    protected abstract void conectar(boolean transaccional) throws ExcepcionConexion;

    protected void conectar() throws ExcepcionConexion {
        this.conectar(false);
    }

    protected boolean estaConectada() throws ExcepcionConexion {
        boolean conectada = false;
        if (this.conexion != null) {
            try {
                conectada = !this.conexion.isClosed();
            } catch (SQLException e) {
                throw new ExcepcionConexion("No se pudo comprobar el estado de la conexión.", e);
            }
        }
        return conectada;
    }

    protected void desconectar() throws ExcepcionConexion {
        if (this.estaConectada()) {
            try {
                this.conexion.close();
            } catch (SQLException e) {
                throw new ExcepcionConexion("No se pudo cerrar la conexión.", e);
            }
        }
        this.transaccional = false;
        this.conexion = null;
    }

    protected void commit() throws ExcepcionConexion {
        if (!this.getTransaccional()) {
            throw new ExcepcionConexion("Se intentó hacer commit sobre una conexión no transaccional.");
        }
        try {
            this.conexion.commit();
        } catch (SQLException e) {
            throw new ExcepcionConexion("No se pudo realizar el commit.", e);
        }
    }

    protected void rollback() throws ExcepcionConexion {
        if (!this.getTransaccional()) {
            throw new ExcepcionConexion("Se intentó hacer rollback sobre una conexión no transaccional.");
        }
        try {
            this.conexion.rollback();
        } catch (SQLException e) {
            throw new ExcepcionConexion("No se pudo realizar el commit.", e);
        }
    }

    private PreparedStatement crearStatement(String sentencia, ArrayList parametros) throws ExcepcionConexion, SQLException {
        if (!this.estaConectada()) {
            throw new ExcepcionConexion("Se intentó crear un statement sobre una conexión cerrada.");
        }
        PreparedStatement stm = this.getConexion().prepareStatement(sentencia);
        if (parametros != null) {
            int i = 0;
            for (Object parm : parametros) {
                i = i + 1;
                stm.setObject(i, parm);
            }
        }
        return stm;
    }

    protected ResultSet ejecutarConsulta(String sentencia, ArrayList parametros) throws ExcepcionConexion {
        PreparedStatement stm = null;
        ResultSet resultado = null;
        try {
            stm = this.crearStatement(sentencia, parametros);
            resultado = stm.executeQuery();
        } catch (SQLException e) {
            throw new ExcepcionConexion("No se pudo ejecutar la consulta.", e);
        } finally {
            if (stm != null) {
                try {
                    stm.closeOnCompletion();
                } catch (SQLException e) {
                    throw new ExcepcionConexion("No se pudo marcar el cerrado del statement utilizado para la consulta.", e);
                }
            }
        }
        return resultado;
    }

    protected int ejecutarSentencia(String sentencia, ArrayList parametros) throws ExcepcionConexion {
        PreparedStatement stm = null;
        int resultado = 0;
        try {
            stm = this.crearStatement(sentencia, parametros);
            resultado = stm.executeUpdate();
        } catch (SQLException e) {
            throw new ExcepcionConexion("No se pudo ejecutar la sentencia.", e);
        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) {
                    throw new ExcepcionConexion("No se pudo cerrar el statement usado para la sentencia.", e);
                }
            }
        }
        return resultado;
    }

    protected int ejecutarProcedimiento(String sentencia, ArrayList parametros) throws ExcepcionConexion {
        CallableStatement stm = null;
        int resultado = 0;
        try {
            stm = this.crearCallableStatement(sentencia, parametros);
            resultado = stm.executeUpdate();
            if(resultado > 0)
                resultado = stm.getInt(1);
        } catch (SQLException e) {
            throw new ExcepcionConexion("No se pudo ejecutar la sentencia.", e);
        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) {
                    throw new ExcepcionConexion("No se pudo cerrar el statement usado para la sentencia.", e);
                }
            }
        }
        return resultado;
    }

    private CallableStatement crearCallableStatement(String sentencia, ArrayList parametros) throws ExcepcionConexion, SQLException{
        if (!this.estaConectada()) {
            throw new ExcepcionConexion("Se intentó crear un statement sobre una conexión cerrada.");
        }
        CallableStatement stm = this.getConexion().prepareCall(sentencia);
        if (parametros != null) {
            int i = 0;
            stm.registerOutParameter(1, java.sql.Types.INTEGER);
            for (Object parm : parametros) {
                i = i + 1;
                stm.setObject(i, parm);
            }
        }
        return stm;
    }
}
