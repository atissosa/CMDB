package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Service;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorServiceSqlServer extends MapeadorService {
    
    protected MapeadorServiceSqlServer(Service service){
        super(service);
    }
    
    
    @Override
    protected String sqlInsertar() {
        return "exec [dbo].[SERVICE_INSERT] @id=?, @nombre=?, @activo=?, @descripcion=?, @direccion=? ";
    }
    
    @Override
    protected ArrayList parametrosInsertar() {
        ArrayList parametros = new ArrayList();
        Service s = this.getService();
        if (s != null){
            parametros.add(s.getId());
            parametros.add(s.getNombre());
            parametros.add(s.getActivo());
            parametros.add(s.getDescripcion());
            parametros.add(s.getDireccion());
        }
        return parametros;
    }
    
    @Override
    protected String sqlActualizar() {
        return "exec [dbo].[SERVICE_UPDATE] @id=?, @nombre=?, @activo=?, @descripcion=?, @direccion=? ";
    }
    
    @Override
    protected ArrayList parametrosActualizar() {
        ArrayList parametros = new ArrayList();
        Service s = this.getService();
        if (s != null){
            parametros.add(s.getId());
            parametros.add(s.getNombre());
            parametros.add(s.getActivo());
            parametros.add(s.getDescripcion());
            parametros.add(s.getDireccion());
        }
        return parametros;
    }
    
    @Override
    protected String sqlEliminar() {
        return "UPDATE Lugares SET Activo = 0 WHERE Id = ?;";
    }
    
    @Override
    protected ArrayList parametrosEliminar() {
        ArrayList parametros = new ArrayList();
        Service s = this.getService();
        if (s != null)
            parametros.add(s.getId());
        return parametros;
    }
    
    @Override
    protected String sqlBuscar() {
        String sql = "SELECT * FROM VServicios";
        String condicion = "";
        if(this.getService().getId() != null)
            condicion += " Id = ?";
        if(this.getService().getNombre() != null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " Nombre = ?";
        }
        if(!condicion.equals(""))
            condicion = " WHERE" + condicion;
        return sql + condicion + " ORDER BY Nombre;";
    }
    
    @Override
    protected ArrayList parametrosBuscar() {
        ArrayList parametros = new ArrayList();
        if(this.getService().getId() != null)
            parametros.add(this.getService().getId());
        if(this.getService().getNombre() != null)
            parametros.add(this.getService().getNombre());
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<Service> services = new ArrayList();
        Service service;
        try {
            while(rs.next()) {
                service = new Service();
                service.setId(rs.getInt("Id"));
                if(rs.wasNull())
                    service.setId(null);
                service.setNombre(rs.getString("Nombre"));
                service.setDescripcion(rs.getString("Descripcion"));
                service.setDireccion(rs.getString("Direccion"));
                service.setActivo(rs.getBoolean("Activo"));
                services.add(service);
            }
            if(services.size() == 1) {
                service = services.get(0);
                this.getService().setId(service.getId());
                this.getService().setNombre(service.getNombre());
                this.getService().setDescripcion(service.getDescripcion());
                this.getService().setDireccion(service.getDireccion());
                this.getService().setActivo(service.getActivo());
                services.set(0, this.getService());
            }
            else {
                this.setService(null);
            }
        }
        catch(SQLException e) {
            services.clear();
            this.setService(null);
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
        return services;
    }
}
