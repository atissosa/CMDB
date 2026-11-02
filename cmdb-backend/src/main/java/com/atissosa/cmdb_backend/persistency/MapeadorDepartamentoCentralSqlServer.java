package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.DepartamentoCentral;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorDepartamentoCentralSqlServer extends MapeadorDepartamentoCentral {
    
    protected MapeadorDepartamentoCentralSqlServer(DepartamentoCentral departamento){
        super(departamento);
    }
    
    
    @Override
    protected String sqlInsertar() {
        String sql = "exec [dbo].[DEPARTAMENTO_CENTRAL_INSERT] @id=?, @nombre=?, @activo=?, @descripcion=?";
        return sql;
    }
    
    @Override
    protected ArrayList parametrosInsertar() {
        ArrayList parametros = new ArrayList();
        DepartamentoCentral d = this.getDepartamento();
        if (d != null){
            parametros.add(d.getId());
            parametros.add(d.getNombre());
            parametros.add(d.getActivo());
            parametros.add(d.getDescripcion());
        }
        return parametros;
    }
    
    @Override
    protected String sqlActualizar() {
        return "exec [dbo].[DEPARTAMENTO_CENTRAL_UPDATE] @id=?, @nombre=?, @activo=?, @descripcion=?";
    }
    
    @Override
    protected ArrayList parametrosActualizar() {
        ArrayList parametros = new ArrayList();
        DepartamentoCentral d = this.getDepartamento();
        if (d != null){
            parametros.add(d.getId());
            parametros.add(d.getNombre());
            parametros.add(d.getActivo());
            parametros.add(d.getDescripcion());
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
        DepartamentoCentral d = this.getDepartamento();
        if (d != null)
            parametros.add(d.getId());
        return parametros;
    }
    
    @Override
    protected String sqlBuscar() {
        String sql = "SELECT * FROM VDepartamentos";
        String condicion = "";
        if(this.getDepartamento().getId() != null)
            condicion += " Id = ?";
        if(this.getDepartamento().getNombre() != null) {
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
        if(this.getDepartamento().getId() != null)
            parametros.add(this.getDepartamento().getId());
        if(this.getDepartamento().getNombre() != null)
            parametros.add(this.getDepartamento().getNombre());
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<DepartamentoCentral> departamentos = new ArrayList();
        DepartamentoCentral departamento;
        try {
            while(rs.next()) {
                departamento = new DepartamentoCentral();
                departamento.setId(rs.getInt("Id"));
                if(rs.wasNull())
                    departamento.setId(null);
                departamento.setNombre(rs.getString("Nombre"));
                departamento.setDescripcion(rs.getString("Descripcion"));
                departamento.setActivo(rs.getBoolean("Activo"));
                departamentos.add(departamento);
            }
            if(departamentos.size() == 1) {
                departamento = departamentos.get(0);
                this.getDepartamento().setId(departamento.getId());
                this.getDepartamento().setNombre(departamento.getNombre());
                this.getDepartamento().setDescripcion(departamento.getDescripcion());
                this.getDepartamento().setActivo(departamento.getActivo());
                departamentos.set(0, this.getDepartamento());
            }
            else {
                this.setDepartamento(null);
            }
        }
        catch(SQLException e) {
            departamentos.clear();
            this.setDepartamento(null);
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
        return departamentos;
    }
    
}
