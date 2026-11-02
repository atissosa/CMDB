package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorMarcasSqlServer extends MapeadorMarcas {

    public MapeadorMarcasSqlServer() {
    }

    @Override
    protected String sqlInsertar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ArrayList parametrosInsertar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String sqlActualizar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ArrayList parametrosActualizar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String sqlEliminar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ArrayList parametrosEliminar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String sqlBuscar() {
        String sql = "SELECT * FROM VMarcas";
        return sql;
    }

    @Override
    protected ArrayList parametrosBuscar() {
        return new ArrayList();
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
         ArrayList<String> marcas = new ArrayList();
        try {
            while(rs.next()) {
                marcas.add(rs.getString("marca"));
            }
        }
        catch(SQLException e) {
            marcas.clear();
            throw new ExcepcionConexion("No se pudo cargar el o los registros de la BD.", e);
        }
        finally {
            try {
                rs.close();
            }
            catch(SQLException e) {
                throw new ExcepcionConexion("No se pudo cerrar el ResultSet traído por la consulta.", e);
            }
        }
        return marcas;
    }
    
}
