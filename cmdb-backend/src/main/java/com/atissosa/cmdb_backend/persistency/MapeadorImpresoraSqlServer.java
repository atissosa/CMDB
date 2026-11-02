package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Equipo.EstadosEquipos;
import com.atissosa.cmdb_backend.domain.Impresora;
import com.atissosa.cmdb_backend.domain.Impresora.ConexionesImpresora;
import com.atissosa.cmdb_backend.domain.Impresora.OpcionesImpresora;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorImpresoraSqlServer extends MapeadorImpresora {
    
    protected MapeadorImpresoraSqlServer(Impresora impresora){
        super(impresora);
    }
    
    protected MapeadorImpresoraSqlServer(DTOFiltroEquipo filtro){
        super(filtro);
    }
    
    
    @Override
    protected String sqlInsertar() {
        return "exec [dbo].[IMPRESORA_INSERT] @Id=?, @Codigo=?, @NumeroDeSerie=?, @Marca=?, @Modelo=?, @PrecioDeCompra=?, @FechaDeAlta=?, @Descripcion=?, @MesesGarantia=?, @EsPrestamo=?, @NumeroPrecinto=?, @Estado=?, @IdUsuario=?, @IdLugarPropietario=?, @DirIp=?, @Conexion=?, @Opcion=?, @Multifuncion=?, @NombreHost=?;";
    }
    
    @Override
    protected ArrayList parametrosInsertar() {
        ArrayList parametros = new ArrayList();
        Impresora i = this.getImpresora();
        if (i != null){
            parametros.add(i.getId());
            parametros.add(i.getCodigo());
            parametros.add(i.getNumeroDeSerie());
            parametros.add(i.getMarca());
            parametros.add(i.getModelo());
            parametros.add(i.getPrecioDeCompra());
            parametros.add(i.getFechaAlta());
            parametros.add(i.getDescripcion());
            parametros.add(i.getMesesGarantia());
            parametros.add(i.getEsPrestamo());
            parametros.add(i.getNumeroPrecinto());
            parametros.add(i.getEstado().toString());
            parametros.add(i.getUsuario().getId());
            parametros.add(i.getLugar().getId());
            parametros.add(i.getIp());
            parametros.add(i.getConexion().toString());
            parametros.add(i.getOpcion().toString());
            parametros.add(i.getMultifuncion());
            parametros.add(i.getNombreHost());
        }
        return parametros;
    }
    
    @Override
    protected String sqlActualizar() {
        return "exec [dbo].[IMPRESORA_UPDATE] @Id=?, @Codigo=?, @NumeroDeSerie=?, @Marca=?, @Modelo=?, @PrecioDeCompra=?, @FechaDeAlta=?, @Descripcion=?, @MesesGarantia=?, @EsPrestamo=?, @NumeroPrecinto=?, @Estado=?, @IdUsuario=?, @IdLugarPropietario=?, @DirIp=?, @Conexion=?, @Opcion=?, @Multifuncion=?, @NombreHost=?, @IdEquipoPrestamo=?;";
    }
    
    @Override
    protected ArrayList parametrosActualizar() {
        ArrayList parametros = new ArrayList();
        Impresora i = this.getImpresora();
        if (i != null){
            parametros.add(i.getId());
            parametros.add(i.getCodigo());
            parametros.add(i.getNumeroDeSerie());
            parametros.add(i.getMarca());
            parametros.add(i.getModelo());
            parametros.add(i.getPrecioDeCompra());
            parametros.add(i.getFechaAlta());
            parametros.add(i.getDescripcion());
            parametros.add(i.getMesesGarantia());
            parametros.add(i.getEsPrestamo());
            parametros.add(i.getNumeroPrecinto());
            parametros.add(i.getEstado().toString());
            parametros.add(i.getUsuario().getId());
            parametros.add(i.getLugar().getId());
            parametros.add(i.getIp());
            parametros.add(i.getConexion().toString());
            parametros.add(i.getOpcion().toString());
            parametros.add(i.getMultifuncion());
            parametros.add(i.getNombreHost());
            parametros.add((i.getEquipoPrestamo() == null) ? null : i.getEquipoPrestamo().getId());
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
        Impresora i = this.getImpresora();
        if (i != null){
            parametros.add(EstadosEquipos.BAJA.toString());
            parametros.add(i.getId());
        }
        return parametros;
    }
    
    @Override
    protected String sqlBuscar() {
        String sql = "SELECT * FROM VImpresoras";
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
            if(this.getImpresora().getId() != null)
                condicion += "Id = ?";
            if(this.getImpresora().getCodigo() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " Codigo = ?";
            }
            if(this.getImpresora().getNumeroDeSerie() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " NumeroDeSerie = ?";
            }
            if(this.getImpresora().getMarca() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " Marca = ?";
            }
            if(this.getImpresora().getModelo() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " Modelo = ?";
            }
        }
        if(!condicion.equals(""))
            condicion = " WHERE " + condicion;
        return sql + condicion + ";";
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
            if(this.getFiltro().tieneEstado())
                parametros.addAll(this.getFiltro().getEstado());
            if(this.getFiltro().tieneIdLugar())
                parametros.addAll(this.getFiltro().getIdLugar());
        }
        else{
            if(this.getImpresora().getId() != null)
                parametros.add(this.getImpresora().getId());
            if(this.getImpresora().getCodigo() != null)
                parametros.add(this.getImpresora().getCodigo());
            if(this.getImpresora().getNumeroDeSerie() != null)
                parametros.add(this.getImpresora().getNumeroDeSerie());
            if(this.getImpresora().getMarca() != null)
                parametros.add(this.getImpresora().getMarca());
            if(this.getImpresora().getModelo() != null)
                parametros.add(this.getImpresora().getModelo());
        }
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<Impresora> impresoras = new ArrayList();
        Impresora impresora;
        try {
            while(rs.next()) {
                impresora = new Impresora();
                impresora.setId(rs.getInt("Id"));
                if(rs.wasNull())
                    impresora.setId(null);
                impresora.setCodigo(rs.getString("Codigo"));
                impresora.setNumeroDeSerie(rs.getString("NumeroDeSerie"));
                impresora.setMarca(rs.getString("Marca"));
                impresora.setModelo(rs.getString("Modelo"));
                impresora.setPrecioDeCompra(rs.getDouble("PrecioDeCompra"));
                impresora.setFechaAlta(rs.getTimestamp("FechaDeAlta"));
                impresora.setDescripcion(rs.getString("Descripcion"));
                impresora.setMesesGarantia(rs.getInt("MesesGarantia"));
                if(rs.wasNull())
                    impresora.setMesesGarantia(null);
                impresora.setEsPrestamo(rs.getBoolean("EsPrestamo"));
                impresora.setNumeroPrecinto(rs.getInt("NumeroPrecinto"));
                if(rs.wasNull())
                    impresora.setNumeroPrecinto(null);
                impresora.setEstado(EstadosEquipos.valueOf(rs.getString("Estado")));
                impresora.getUsuario().setId(rs.getInt("IdUsuario"));
                if(rs.wasNull())
                    impresora.getUsuario().setId(null);
                impresora.getUsuario().setUsuario(rs.getString("Usuario"));
                impresora.setIdLugar(rs.getInt("IdLugarPropietario"));
                if(rs.wasNull())
                    impresora.setIdLugar(null);
                impresora.setIp(rs.getString("DirIp"));
                impresora.setConexion(ConexionesImpresora.valueOf(rs.getString("Conexion")));
                impresora.setOpcion(OpcionesImpresora.valueOf(rs.getString("Opcion")));
                impresora.setMultifuncion(rs.getBoolean("Multifuncion"));
                impresora.setNombreHost(rs.getString("NombreHost"));
                impresora.setIdEquipoPrestamo(rs.getInt("IdEquipoPrestamo"));
                if(rs.wasNull())
                    impresora.setIdEquipoPrestamo(null);
                impresora.setNombreLugarActual(rs.getString("LugarActual"));
                impresoras.add(impresora);
            }
            if(impresoras.size() == 1) {
                impresora = impresoras.get(0);
                this.getImpresora().setId(impresora.getId());
                this.getImpresora().setCodigo(impresora.getCodigo());
                this.getImpresora().setNumeroDeSerie(impresora.getNumeroDeSerie());
                this.getImpresora().setMarca(impresora.getMarca());
                this.getImpresora().setModelo(impresora.getModelo());
                this.getImpresora().setPrecioDeCompra(impresora.getPrecioDeCompra());
                this.getImpresora().setFechaAlta(impresora.getFechaAlta());
                this.getImpresora().setDescripcion(impresora.getDescripcion());
                this.getImpresora().setMesesGarantia(impresora.getMesesGarantia());
                this.getImpresora().setEsPrestamo(impresora.getEsPrestamo());
                this.getImpresora().setNumeroPrecinto(impresora.getNumeroPrecinto());
                this.getImpresora().setEstado(impresora.getEstado());
                this.getImpresora().setUsuario(impresora.getUsuario());
                this.getImpresora().setIdLugar(impresora.getIdLugar());
                this.getImpresora().setIp(impresora.getIp());
                this.getImpresora().setConexion(impresora.getConexion());
                this.getImpresora().setOpcion(impresora.getOpcion());
                this.getImpresora().setMultifuncion(impresora.getMultifuncion());
                this.getImpresora().setNombreHost(impresora.getNombreHost());
                this.getImpresora().setIdEquipoPrestamo(impresora.getIdEquipoPrestamo());
                this.getImpresora().setNombreLugarActual(impresora.getNombreLugarActual());
                impresoras.set(0, this.getImpresora());
            }
            else {
                this.setImpresora(null);
            }
        }
        catch(SQLException e) {
            impresoras.clear();
            this.setImpresora(null);
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
        return impresoras;
    }
    
}
