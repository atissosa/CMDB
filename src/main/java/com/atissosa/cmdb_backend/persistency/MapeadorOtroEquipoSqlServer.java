package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Equipo.EstadosEquipos;
import com.atissosa.cmdb_backend.domain.OtroEquipo;
import com.atissosa.cmdb_backend.domain.OtroEquipo.TiposEquipo;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorOtroEquipoSqlServer extends MapeadorOtroEquipo {
    
    protected MapeadorOtroEquipoSqlServer(OtroEquipo otroEquipo){
        super(otroEquipo);
    }
    
    protected MapeadorOtroEquipoSqlServer(DTOFiltroEquipo filtro){
        super(filtro);
    }
    
    
    @Override
    protected String sqlInsertar() {
        return "exec [dbo].[OTRO_EQUIPO_INSERT] @Id=?, @Codigo=?, @NumeroDeSerie=?, @Marca=?, @Modelo=?, @PrecioDeCompra=?, @FechaDeAlta=?, @Descripcion=?, @MesesGarantia=?, @EsPrestamo=?, @NumeroPrecinto=?, @Estado=?, @IdUsuario=?, @IdLugarPropietario=?, @Tipo=?;";
    }
    
    @Override
    protected ArrayList parametrosInsertar() {
        ArrayList parametros = new ArrayList();
        OtroEquipo o = this.getOtroEquipo();
        if (o != null){
            parametros.add(o.getId());
            parametros.add(o.getCodigo());
            parametros.add(o.getNumeroDeSerie());
            parametros.add(o.getMarca());
            parametros.add(o.getModelo());
            parametros.add(o.getPrecioDeCompra());
            parametros.add(o.getFechaAlta());
            parametros.add(o.getDescripcion());
            parametros.add(o.getMesesGarantia());
            parametros.add(o.getEsPrestamo());
            parametros.add(o.getNumeroPrecinto());
            parametros.add(o.getEstado().toString());
            parametros.add(o.getUsuario().getId());
            parametros.add(o.getLugar().getId());
            parametros.add(o.getTipo());
        }
        return parametros;
    }
    
    @Override
    protected String sqlActualizar() {
        return "exec [dbo].[OTRO_EQUIPO_UPDATE] @Id=?, @Codigo=?, @NumeroDeSerie=?, @Marca=?, @Modelo=?, @PrecioDeCompra=?, @FechaDeAlta=?, @Descripcion=?, @MesesGarantia=?, @EsPrestamo=?, @NumeroPrecinto=?, @Estado=?, @IdUsuario=?, @IdLugarPropietario=?, @Tipo=?, @IdEquipoPrestamo=?;";
    }
    
    @Override
    protected ArrayList parametrosActualizar() {
        ArrayList parametros = new ArrayList();
        OtroEquipo o = this.getOtroEquipo();
        if (o != null){
            parametros.add(o.getId());
            parametros.add(o.getCodigo());
            parametros.add(o.getNumeroDeSerie());
            parametros.add(o.getMarca());
            parametros.add(o.getModelo());
            parametros.add(o.getPrecioDeCompra());
            parametros.add(o.getFechaAlta());
            parametros.add(o.getDescripcion());
            parametros.add(o.getMesesGarantia());
            parametros.add(o.getEsPrestamo());
            parametros.add(o.getNumeroPrecinto());
            parametros.add(o.getEstado().toString());
            parametros.add(o.getUsuario().getId());
            parametros.add(o.getLugar().getId());
            parametros.add(o.getTipo());
            parametros.add((o.getEquipoPrestamo() == null) ? null : o.getEquipoPrestamo().getId());
        }
        return parametros;
    }
    
    @Override
    protected String sqlEliminar() {
        return "UPDATE Equipos SET Estado = ? WHERE Id = ?;";
    }
    
    @Override
    protected ArrayList parametrosEliminar() {
        ArrayList parametros = new ArrayList();
        OtroEquipo o = this.getOtroEquipo();
        if (o != null){
            parametros.add(EstadosEquipos.BAJA.toString());
            parametros.add(o.getId());
        }
        return parametros;
    }
    
    @Override
    protected String sqlBuscar() {
        String sql = "SELECT * FROM VOtrosEquipos";
        String condicion = "";
        if(this.getFiltro() != null){
            if(this.getFiltro().tieneId()){
                condicion += "Id IN (";
                for(Object o : this.getFiltro().getId()){
                    if(condicion.endsWith("?"))
                        condicion += ", ";
                    condicion += "?";
                }
                condicion += ")";
            }
            if(this.getFiltro().tieneCodigo()){
                if(!condicion.equals(""))
                    condicion += " AND ";
                condicion += "Codigo IN (";
                for(Object o : this.getFiltro().getCodigo()){
                    if(condicion.endsWith("?"))
                        condicion += ", ";
                    condicion += "?";
                }
                condicion += ")";
            }
            if(this.getFiltro().tieneNumeroSerie()){
                if(!condicion.equals(""))
                    condicion += " AND ";
                condicion += "NumeroDeSerie IN (";
                for(Object o : this.getFiltro().getNumeroSerie()){
                    if(condicion.endsWith("?"))
                        condicion += ", ";
                    condicion += "?";
                }
                condicion += ")";
            }
            if(this.getFiltro().tieneEsPrestamo()){
                if(!condicion.equals(""))
                    condicion += " AND ";
                condicion += "EsPrestamo = ?";
            }
            if(this.getFiltro().tieneUsuario()){
                if(!condicion.equals(""))
                    condicion += " AND ";
                condicion += "Usuario IN (";
                for(Object o : this.getFiltro().getUsuario()){
                    if(condicion.endsWith("?"))
                        condicion += ", ";
                    condicion += "?";
                }
                condicion += ")";
            }
            if(this.getFiltro().tieneTipo()){
                if(!condicion.equals(""))
                    condicion += " AND ";
                condicion += "Tipo IN (";
                for(Object o : this.getFiltro().getTipo()){
                    if(condicion.endsWith("?"))
                        condicion += ", ";
                    condicion += "?";
                }
                condicion += ")";
            }
            if(this.getFiltro().tieneEstado()){
                if(!condicion.equals(""))
                    condicion += " AND ";
                condicion += "Estado IN (";
                for(Object o : this.getFiltro().getEstado()){
                    if(condicion.endsWith("?"))
                        condicion += ", ";
                    condicion += "?";
                }
                condicion += ")";
            }
            if(this.getFiltro().tieneIdLugar()){
                if(!condicion.equals(""))
                    condicion += " AND ";
                condicion += "IdLugarPropietario IN (";
                for(Object o : this.getFiltro().getIdLugar()){
                    if(condicion.endsWith("?"))
                        condicion += ", ";
                    condicion += "?";
                }
                condicion += ")";
            }
        }
        else{
            if(this.getOtroEquipo().getId() != null)
                condicion += "Id = ?";
            if(this.getOtroEquipo().getCodigo() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " Codigo = ?";
            }
            if(this.getOtroEquipo().getNumeroDeSerie() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " NumeroDeSerie = ?";
            }
            if(this.getOtroEquipo().getMarca() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " Marca = ?";
            }
            if(this.getOtroEquipo().getModelo() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " Modelo = ?";
            }
        }
        if(!condicion.equals(""))
            condicion = " WHERE " + condicion;
        return sql + condicion + " ORDER BY Codigo;";
    }
    
    @Override
    protected ArrayList parametrosBuscar() {
        ArrayList parametros = new ArrayList();
        if(this.getFiltro() != null){
            if(this.getFiltro().tieneId())
                parametros.addAll(this.getFiltro().getId());
            if(this.getFiltro().tieneCodigo())
                parametros.addAll(this.getFiltro().getCodigo());
            if(this.getFiltro().tieneNumeroSerie())
                parametros.addAll(this.getFiltro().getNumeroSerie());
            if(this.getFiltro().tieneEsPrestamo())
                parametros.add(this.getFiltro().getEsPrestamo());
            if(this.getFiltro().tieneUsuario())
                parametros.addAll(this.getFiltro().getUsuario());
            if(this.getFiltro().tieneTipo())
                parametros.addAll(this.getFiltro().getTipo());
            if(this.getFiltro().tieneEstado())
                parametros.addAll(this.getFiltro().getEstado());
            if(this.getFiltro().tieneIdLugar())
                parametros.addAll(this.getFiltro().getIdLugar());
        }
        else{
            if(this.getOtroEquipo().getId() != null)
                parametros.add(this.getOtroEquipo().getId());
            if(this.getOtroEquipo().getCodigo() != null)
                parametros.add(this.getOtroEquipo().getCodigo());
            if(this.getOtroEquipo().getNumeroDeSerie() != null)
                parametros.add(this.getOtroEquipo().getNumeroDeSerie());
            if(this.getOtroEquipo().getMarca() != null)
                parametros.add(this.getOtroEquipo().getMarca());
            if(this.getOtroEquipo().getModelo() != null)
                parametros.add(this.getOtroEquipo().getModelo());
        }
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<OtroEquipo> otrosEquipos = new ArrayList();
        OtroEquipo otroEquipo;
        try {
            while(rs.next()) {
                otroEquipo = new OtroEquipo();
                otroEquipo.setId(rs.getInt("Id"));
                if(rs.wasNull())
                    otroEquipo.setId(null);
                otroEquipo.setCodigo(rs.getString("Codigo"));
                otroEquipo.setNumeroDeSerie(rs.getString("NumeroDeSerie"));
                otroEquipo.setMarca(rs.getString("Marca"));
                otroEquipo.setModelo(rs.getString("Modelo"));
                otroEquipo.setPrecioDeCompra(rs.getDouble("PrecioDeCompra"));
                otroEquipo.setFechaAlta(rs.getTimestamp("FechaDeAlta"));
                otroEquipo.setDescripcion(rs.getString("Descripcion"));
                otroEquipo.setMesesGarantia(rs.getInt("MesesGarantia"));
                if(rs.wasNull())
                    otroEquipo.setMesesGarantia(null);
                otroEquipo.setEsPrestamo(rs.getBoolean("EsPrestamo"));
                otroEquipo.setNumeroPrecinto(rs.getInt("NumeroPrecinto"));
                if(rs.wasNull())
                    otroEquipo.setNumeroPrecinto(null);
                otroEquipo.setEstado(EstadosEquipos.valueOf(rs.getString("Estado")));
                otroEquipo.getUsuario().setId(rs.getInt("IdUsuario"));
                if(rs.wasNull())
                    otroEquipo.getUsuario().setId(null);
                otroEquipo.getUsuario().setUsuario(rs.getString("Usuario"));
                otroEquipo.setIdLugar(rs.getInt("IdLugarPropietario"));
                if(rs.wasNull())
                    otroEquipo.setIdLugar(null);
                otroEquipo.setTipo(TiposEquipo.valueOf(rs.getString("Tipo")));
                otroEquipo.setIdEquipoPrestamo(rs.getInt("IdEquipoPrestamo"));
                if(rs.wasNull())
                    otroEquipo.setIdEquipoPrestamo(null);
                otroEquipo.setNombreLugarActual(rs.getString("LugarActual"));
                otrosEquipos.add(otroEquipo);
            }
            if(otrosEquipos.size() == 1) {
                otroEquipo = otrosEquipos.get(0);
                this.getOtroEquipo().setId(otroEquipo.getId());
                this.getOtroEquipo().setCodigo(otroEquipo.getCodigo());
                this.getOtroEquipo().setNumeroDeSerie(otroEquipo.getNumeroDeSerie());
                this.getOtroEquipo().setMarca(otroEquipo.getMarca());
                this.getOtroEquipo().setModelo(otroEquipo.getModelo());
                this.getOtroEquipo().setPrecioDeCompra(otroEquipo.getPrecioDeCompra());
                this.getOtroEquipo().setFechaAlta(otroEquipo.getFechaAlta());
                this.getOtroEquipo().setDescripcion(otroEquipo.getDescripcion());
                this.getOtroEquipo().setMesesGarantia(otroEquipo.getMesesGarantia());
                this.getOtroEquipo().setEsPrestamo(otroEquipo.getEsPrestamo());
                this.getOtroEquipo().setNumeroPrecinto(otroEquipo.getNumeroPrecinto());
                this.getOtroEquipo().setEstado(otroEquipo.getEstado());
                this.getOtroEquipo().setUsuario(otroEquipo.getUsuario());
                this.getOtroEquipo().setIdLugar(otroEquipo.getIdLugar());
                this.getOtroEquipo().setTipo(TiposEquipo.valueOf(otroEquipo.getTipo()));
                this.getOtroEquipo().setIdEquipoPrestamo(otroEquipo.getIdEquipoPrestamo());
                this.getOtroEquipo().setNombreLugarActual(otroEquipo.getNombreLugarActual());
                otrosEquipos.set(0, this.getOtroEquipo());
            }
            else {
                this.setOtroEquipo(null);
            }
        }
        catch(SQLException e) {
            otrosEquipos.clear();
            this.setOtroEquipo(null);
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
        return otrosEquipos;
    }
    
}
