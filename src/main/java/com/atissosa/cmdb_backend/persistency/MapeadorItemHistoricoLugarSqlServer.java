package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Equipo.EstadosEquipos;
import com.atissosa.cmdb_backend.domain.ItemHistoricoLugar;
import com.atissosa.cmdb_backend.domain.Usuario;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorItemHistoricoLugarSqlServer extends MapeadorItemHistoricoLugar {
    
    protected MapeadorItemHistoricoLugarSqlServer(ItemHistoricoLugar item){
        super(item);
    }
    
    
    @Override
    protected String sqlInsertar() {
        return "exec [dbo].[ITEM_HISTORICO_LUGAR_INSERT] @id=?, @idEquipo=?, @idLugar=?, @descripcion=?, @fecha=?, @idUsuario=?, @estado=?, @idEquipoPrestamo=?";
    }
    
    @Override
    protected ArrayList parametrosInsertar() {
        ArrayList parametros = new ArrayList();
        ItemHistoricoLugar i = this.getItem();
        if (i != null){
            parametros.add(i.getId());
            parametros.add(i.getEquipo().getId());
            parametros.add(i.getLugar().getId());
            parametros.add(i.getDescripcion());
            parametros.add(i.getFecha());
            parametros.add(i.getUsuario().getId());
            parametros.add(i.getEstado().toString());
            parametros.add((i.getEquipoPrestamo() != null) ? i.getEquipoPrestamo().getId() : null);
        }
        return parametros;
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
        String sql = "SELECT * FROM ItemHistoricoLugar";
        String condicion = "";
        if(this.getItem().getId() != null)
            condicion += " Id = ?";
        if(this.getItem().getIdEquipo() != null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " IdEquipo = ?";
        }
        if(this.getItem().getIdLugar() != null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " IdLugar = ?";
        }
        if(this.getItem().getUsuario()!= null && this.getItem().getUsuario().getId() != null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " IdUsuario = ?";
        }
        if(!condicion.equals(""))
            condicion = " WHERE" + condicion;
        return sql + condicion + " ORDER BY IdEquipo, Fecha;";
    }
    
    @Override
    protected ArrayList parametrosBuscar() {
        ArrayList parametros = new ArrayList();
        if(this.getItem().getId() != null)
            parametros.add(this.getItem().getId());
        if(this.getItem().getIdEquipo() != null)
            parametros.add(this.getItem().getIdEquipo());
        if(this.getItem().getIdLugar() != null)
            parametros.add(this.getItem().getIdLugar());
        if(this.getItem().getUsuario()!= null && this.getItem().getUsuario().getId() != null)
            parametros.add(this.getItem().getUsuario().getId());
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<ItemHistoricoLugar> items = new ArrayList();
        ItemHistoricoLugar item;
        try {
            while(rs.next()) {
                item = new ItemHistoricoLugar();
                item.setId(rs.getInt("Id"));
                if(rs.wasNull())
                    item.setId(null);
                item.setIdEquipo(rs.getInt("IdEquipo"));
                if(rs.wasNull())
                    item.setIdEquipo(null);
                item.setIdLugar(rs.getInt("IdLugar"));
                if(rs.wasNull())
                    item.setIdLugar(null);
                item.setDescripcion(rs.getString("Descripcion"));
                item.setFecha(rs.getTimestamp("Fecha"));
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("IdUsuario"));
                if(rs.wasNull())
                    usuario.setId(null);
                item.setUsuario(usuario);
                item.setEstado(EstadosEquipos.valueOf(rs.getString("Estado")));
                item.setIdEquipoPrestamo(rs.getInt("IdEquipoPrestamo"));
                if(rs.wasNull())
                    item.setIdEquipoPrestamo(null);
                items.add(item);
            }
            if(items.size() == 1) {
                item = items.get(0);
                this.getItem().setId(item.getId());
                this.getItem().setIdEquipo(item.getIdEquipo());
                this.getItem().setIdLugar(item.getIdLugar());
                this.getItem().setDescripcion(item.getDescripcion());
                this.getItem().setFecha(item.getFecha());
                this.getItem().setUsuario(item.getUsuario());
                this.getItem().setEstado(item.getEstado());
                this.getItem().setIdEquipoPrestamo(item.getIdEquipoPrestamo());
                items.set(0, this.getItem());
            }
            else {
                this.setItem(null);
            }
        }
        catch(SQLException e) {
            items.clear();
            this.setItem(null);
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
        return items;
    }
    
}
