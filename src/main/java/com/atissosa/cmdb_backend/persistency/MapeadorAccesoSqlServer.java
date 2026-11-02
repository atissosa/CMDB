package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Acceso;
import com.atissosa.cmdb_backend.domain.Rol;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorAccesoSqlServer extends MapeadorAcceso {
    
    protected MapeadorAccesoSqlServer(Acceso acceso){
        super(acceso);
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
        String sql = "SELECT * FROM VAccesosRoles";
        String condicion = "";
        if(this.getAcceso().getOperacion() != null)
            condicion += " Operacion = ?";
        if(this.getAcceso().getRol() != null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " IdRol = ?";
        }
        if(!condicion.equals(""))
            condicion = " WHERE" + condicion;
        return sql + condicion + " ORDER BY IdRol, Operacion;";
    }
    
    @Override
    protected ArrayList parametrosBuscar() {
        ArrayList parametros = new ArrayList();
        if(this.getAcceso().getOperacion() != null)
            parametros.add(this.getAcceso().getOperacion());
        if(this.getAcceso().getRol() != null)
            parametros.add(this.getAcceso().getRol().getId());
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<Acceso> accesos = new ArrayList();
        Acceso acceso;
        Rol rol;
        try {
            while(rs.next()) {
                acceso = new Acceso();
                acceso.setOperacion(rs.getString("Operacion"));
                rol = new Rol();
                rol.setId(rs.getInt("IdRol"));
                if(rs.wasNull())
                    rol.setId(null);
                rol.setNombre(rs.getString("NombreRol"));
                acceso.setRol(rol);
                accesos.add(acceso);
            }
            if(accesos.size() == 1) {
                acceso = accesos.get(0);
                this.getAcceso().setOperacion(acceso.getOperacion());
                this.getAcceso().setRol(acceso.getRol());
                accesos.set(0, this.getAcceso());
            }
            else {
                this.setAcceso(null);
            }
        }
        catch(SQLException e) {
            accesos.clear();
            this.setAcceso(null);
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
        return accesos;
    }
    
}
