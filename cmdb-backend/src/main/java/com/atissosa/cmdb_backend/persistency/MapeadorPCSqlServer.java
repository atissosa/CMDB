package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Equipo.EstadosEquipos;
import com.atissosa.cmdb_backend.domain.PC;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorPCSqlServer extends MapeadorPC {
    
    protected MapeadorPCSqlServer(PC pc){
        super(pc);
    }
    
    protected MapeadorPCSqlServer(DTOFiltroEquipo filtro){
        super(filtro);
    }
    
    
    @Override
    protected String sqlInsertar() {
        return "exec [dbo].[PC_INSERT] @Id=?, @Codigo=?, @NumeroDeSerie=?, @Marca=?, @Modelo=?, @PrecioDeCompra=?, @FechaDeAlta=?, @Descripcion=?, @MesesGarantia=?, @EsPrestamo=?, @NumeroPrecinto=?, @Estado=?, @IdUsuario=?, @IdLugarPropietario=?, @DirIp=?, @NombreHost=?, @Procesador=?, @MemoriaRam=?, @TamanoDisco=?;";
    }
    
    @Override
    protected ArrayList parametrosInsertar() {
        ArrayList parametros = new ArrayList();
        PC p = this.getPC();
        if (p != null){
            parametros.add(p.getId());
            parametros.add(p.getCodigo());
            parametros.add(p.getNumeroDeSerie());
            parametros.add(p.getMarca());
            parametros.add(p.getModelo());
            parametros.add(p.getPrecioDeCompra());
            parametros.add(p.getFechaAlta());
            parametros.add(p.getDescripcion());
            parametros.add(p.getMesesGarantia());
            parametros.add(p.getEsPrestamo());
            parametros.add(p.getNumeroPrecinto());
            parametros.add(p.getEstado().toString());
            parametros.add(p.getUsuario().getId());
            parametros.add(p.getLugar().getId());
            parametros.add(p.getIp());
            parametros.add(p.getNombreHost());
            parametros.add(p.getProcesador());
            parametros.add(p.getMemoriaRam());
            parametros.add(p.getTamanoDisco());
        }
        return parametros;
    }
    
    @Override
    protected String sqlActualizar() {
        return "exec [dbo].[PC_UPDATE] @Id=?, @Codigo=?, @NumeroDeSerie=?, @Marca=?,"
                + " @Modelo=?, @PrecioDeCompra=?, @FechaDeAlta=?, @Descripcion=?,"
                + " @MesesGarantia=?, @EsPrestamo=?, @NumeroPrecinto=?, @Estado=?,"
                + " @IdUsuario=?, @IdLugarPropietario=?, @DirIp=?, @NombreHost=?,"
                + " @Procesador=?, @MemoriaRam=?, @TamanoDisco=?, @IdEquipoPrestamo=?;";
    }
    
    @Override
    protected ArrayList parametrosActualizar() {
        ArrayList parametros = new ArrayList();
        PC p = this.getPC();
        if (p != null){
            parametros.add(p.getId());
            parametros.add(p.getCodigo());
            parametros.add(p.getNumeroDeSerie());
            parametros.add(p.getMarca());
            parametros.add(p.getModelo());
            parametros.add(p.getPrecioDeCompra());
            parametros.add(p.getFechaAlta());
            parametros.add(p.getDescripcion());
            parametros.add(p.getMesesGarantia());
            parametros.add(p.getEsPrestamo());
            parametros.add(p.getNumeroPrecinto());
            parametros.add(p.getEstado().toString());
            parametros.add(p.getUsuario().getId());
            parametros.add(p.getLugar().getId());
            parametros.add(p.getIp());
            parametros.add(p.getNombreHost());
            parametros.add(p.getProcesador());
            parametros.add(p.getMemoriaRam());
            parametros.add(p.getTamanoDisco());
            parametros.add((p.getEquipoPrestamo() == null) ? null : p.getEquipoPrestamo().getId());
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
        PC p = this.getPC();
        if (p != null){
            parametros.add(EstadosEquipos.BAJA.toString());
            parametros.add(p.getId());
        }
        return parametros;
    }
    
    @Override
    protected String sqlBuscar() {
        String sql = "SELECT * FROM VPCs";
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
            if(this.getPC().getId() != null)
                condicion += "Id = ?";
            if(this.getPC().getCodigo() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " Codigo = ?";
            }
            if(this.getPC().getNumeroDeSerie() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " NumeroDeSerie = ?";
            }
            if(this.getPC().getMarca() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " Marca = ?";
            }
            if(this.getPC().getModelo() != null) {
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
            if(this.getFiltro().tieneEstado())
                parametros.addAll(this.getFiltro().getEstado());
            if(this.getFiltro().tieneIdLugar())
                parametros.addAll(this.getFiltro().getIdLugar());
        }
        else{
            if(this.getPC().getId() != null)
                parametros.add(this.getPC().getId());
            if(this.getPC().getCodigo() != null)
                parametros.add(this.getPC().getCodigo());
            if(this.getPC().getNumeroDeSerie() != null)
                parametros.add(this.getPC().getNumeroDeSerie());
            if(this.getPC().getMarca() != null)
                parametros.add(this.getPC().getMarca());
            if(this.getPC().getModelo() != null)
                parametros.add(this.getPC().getModelo());
        }
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<PC> pcs = new ArrayList();
        PC pc;
        try {
            while(rs.next()) {
                pc = new PC();
                pc.setId(rs.getInt("Id"));
                if(rs.wasNull())
                    pc.setId(null);
                pc.setCodigo(rs.getString("Codigo"));
                pc.setNumeroDeSerie(rs.getString("NumeroDeSerie"));
                pc.setMarca(rs.getString("Marca"));
                pc.setModelo(rs.getString("Modelo"));
                pc.setPrecioDeCompra(rs.getDouble("PrecioDeCompra"));
                pc.setFechaAlta(rs.getTimestamp("FechaDeAlta"));
                pc.setDescripcion(rs.getString("Descripcion"));
                pc.setMesesGarantia(rs.getInt("MesesGarantia"));
                if(rs.wasNull())
                    pc.setMesesGarantia(null);
                pc.setEsPrestamo(rs.getBoolean("EsPrestamo"));
                pc.setNumeroPrecinto(rs.getInt("NumeroPrecinto"));
                if(rs.wasNull())
                    pc.setNumeroPrecinto(null);
                pc.setEstado(EstadosEquipos.valueOf(rs.getString("Estado")));
                pc.getUsuario().setId(rs.getInt("IdUsuario"));
                if(rs.wasNull())
                    pc.getUsuario().setId(null);
                pc.getUsuario().setUsuario(rs.getString("Usuario"));
                pc.setIdLugar((rs.getInt("IdLugarPropietario")));
                if(rs.wasNull())
                    pc.setIdLugar(null);
                pc.setIp(rs.getString("DirIp"));
                pc.setNombreHost(rs.getString("NombreHost"));
                pc.setProcesador(rs.getString("Procesador"));
                pc.setMemoriaRam(rs.getInt("MemoriaRam"));
                if(rs.wasNull())
                    pc.setMemoriaRam(null);
                pc.setTamanoDisco(rs.getInt("TamanoDisco"));
                if(rs.wasNull())
                    pc.setTamanoDisco(null);
                pc.setIdEquipoPrestamo(rs.getInt("IdEquipoPrestamo"));
                if(rs.wasNull())
                    pc.setIdEquipoPrestamo(null);
                pc.setNombreLugarActual(rs.getString("LugarActual"));
                pcs.add(pc);
            }
            if(pcs.size() == 1) {
                pc = pcs.get(0);
                this.getPC().setId(pc.getId());
                this.getPC().setCodigo(pc.getCodigo());
                this.getPC().setNumeroDeSerie(pc.getNumeroDeSerie());
                this.getPC().setMarca(pc.getMarca());
                this.getPC().setModelo(pc.getModelo());
                this.getPC().setPrecioDeCompra(pc.getPrecioDeCompra());
                this.getPC().setFechaAlta(pc.getFechaAlta());
                this.getPC().setDescripcion(pc.getDescripcion());
                this.getPC().setMesesGarantia(pc.getMesesGarantia());
                this.getPC().setEsPrestamo(pc.getEsPrestamo());
                this.getPC().setNumeroPrecinto(pc.getNumeroPrecinto());
                this.getPC().setEstado(pc.getEstado());
                this.getPC().setUsuario(pc.getUsuario());
                this.getPC().setIdLugar(pc.getIdLugar());
                this.getPC().setIp(pc.getIp());
                this.getPC().setNombreHost(pc.getNombreHost());
                this.getPC().setProcesador(pc.getProcesador());
                this.getPC().setMemoriaRam(pc.getMemoriaRam());
                this.getPC().setTamanoDisco(pc.getTamanoDisco());
                this.getPC().setIdEquipoPrestamo(pc.getIdEquipoPrestamo());
                this.getPC().setNombreLugarActual(pc.getNombreLugarActual());
                pcs.set(0, this.getPC());
            }
            else {
                this.setPC(null);
            }
        }
        catch(SQLException e) {
            pcs.clear();
            this.setPC(null);
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
        return pcs;
    }
    
}
