package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.DepartamentoCentral;
import com.atissosa.cmdb_backend.domain.Rol;
import com.atissosa.cmdb_backend.domain.Usuario;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorUsuarioSqlServer extends MapeadorUsuario {
    
    protected MapeadorUsuarioSqlServer(Usuario usuario){
        super(usuario);
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
        String sql = "SELECT * FROM VUsuarios";
        String condicion = "";
        if(this.getUsuario().getId() != null)
            condicion += " Id = ?";
        if(this.getUsuario().getUsuario() != null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " Usuario = ?";
        }
        if(!condicion.equals(""))
            condicion = " WHERE" + condicion;
        return sql + condicion + " ORDER BY Usuario;";
    }
    
    @Override
    protected ArrayList parametrosBuscar() {
        ArrayList parametros = new ArrayList();
        if(this.getUsuario().getId() != null)
            parametros.add(this.getUsuario().getId());
        if(this.getUsuario().getUsuario() != null)
            parametros.add(this.getUsuario().getUsuario());
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<Usuario> usuarios = new ArrayList();
        Usuario usuario;
        Rol rol;
        DepartamentoCentral departamento;
        try {
            while(rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("Id"));
                if(rs.wasNull())
                    usuario.setId(null);
                usuario.setUsuario(rs.getString("Usuario"));
                usuario.setContrasena(rs.getString("Contrasena"));
                usuario.setActivo(rs.getBoolean("Activo"));
                rol = new Rol();
                rol.setId(rs.getInt("IdRol"));
                if(rs.wasNull())
                    rol.setId(null);
                rol.setNombre(rs.getString("NombreRol"));
                usuario.setRol(rol);
                departamento = new DepartamentoCentral();
                departamento.setId(rs.getInt("IdDepartamento"));
                if(rs.wasNull())
                    departamento.setId(null);
                departamento.setNombre(rs.getString("NombreDepartamento"));
                departamento.setDescripcion(rs.getString("DescripcionDepartamento"));
                departamento.setActivo(rs.getBoolean("ActivoDepartamento"));
                usuario.setDepartamento(departamento);
                usuarios.add(usuario);
            }
            if(usuarios.size() == 1) {
                usuario = usuarios.get(0);
                this.getUsuario().setId(usuario.getId());
                this.getUsuario().setUsuario(usuario.getUsuario());
                this.getUsuario().setContrasena(usuario.getContrasena());
                this.getUsuario().setActivo(usuario.getActivo());
                this.getUsuario().setRol(usuario.getRol());
                this.getUsuario().setDepartamento(usuario.getDepartamento());
                usuarios.set(0, this.getUsuario());
            }
            else {
                this.setUsuario(null);
            }
        }
        catch(SQLException e) {
            usuarios.clear();
            this.setUsuario(null);
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
        return usuarios;
    }
    
}
