package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.SubAgencia;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorSubAgenciaSqlServer extends MapeadorSubAgencia {
    
    protected MapeadorSubAgenciaSqlServer(SubAgencia subagencia){
        super(subagencia);
    }
    
    
    @Override
    protected String sqlInsertar() {
        return null;
    }
    
    @Override
    protected ArrayList parametrosInsertar() {
        return null;
    }
    
    @Override
    protected String sqlActualizar() {
        return null;
    }
    
    @Override
    protected ArrayList parametrosActualizar() {
        return null;
    }
    
    @Override
    protected String sqlEliminar() {
        return null;
    }
    
    @Override
    protected ArrayList parametrosEliminar() {
        return null;
    }
    
    @Override
    protected String sqlBuscar() {
        String sql = "SELECT * FROM VSubagencias";
        String condicion = "";
        if(this.getSubagencia().getId() != null)
            condicion += " Id = ?";
        if(this.getSubagencia().getNombre() != null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " Nombre = ?";
        }
        if(this.getSubagencia().getNumero() != null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " Numero = ?";
        }
        if(!condicion.equals(""))
            condicion = " WHERE" + condicion;
        return sql + condicion + " ORDER BY Numero;";
    }
    
    @Override
    protected ArrayList parametrosBuscar() {
        ArrayList parametros = new ArrayList();
        if(this.getSubagencia().getId() != null)
            parametros.add(this.getSubagencia().getId());
        if(this.getSubagencia().getNombre() != null)
            parametros.add(this.getSubagencia().getNombre());
        if(this.getSubagencia().getNumero() != null)
            parametros.add(this.getSubagencia().getNumero());
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<SubAgencia> subagencias = new ArrayList();
        SubAgencia subagencia;
        try {
            while(rs.next()) {
                subagencia = new SubAgencia();
                subagencia.setId(rs.getInt("Id"));
                if(rs.wasNull())
                    subagencia.setId(null);
                subagencia.setNombre(rs.getString("Nombre"));
                subagencia.setNumero(rs.getInt("Numero"));
                if(rs.wasNull())
                    subagencia.setNumero(null);
                subagencia.setActivo(rs.getBoolean("Activo"));
                subagencias.add(subagencia);
            }
            if(subagencias.size() == 1) {
                subagencia = subagencias.get(0);
                this.getSubagencia().setId(subagencia.getId());
                this.getSubagencia().setNombre(subagencia.getNombre());
                this.getSubagencia().setNumero(subagencia.getNumero());
                this.getSubagencia().setActivo(subagencia.getActivo());
                subagencias.set(0, this.getSubagencia());
            }
            else {
                this.setSubagencia(null);
            }
        }
        catch(SQLException e) {
            subagencias.clear();
            this.setSubagencia(null);
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
        return subagencias;
    }
    
}
