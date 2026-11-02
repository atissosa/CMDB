package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Equipo.EstadosEquipos;
import com.atissosa.cmdb_backend.domain.Validadora;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorValidadoraSqlServer extends MapeadorValidadora {
    
    protected MapeadorValidadoraSqlServer(Validadora validadora){
        super(validadora);
    }
    
    protected MapeadorValidadoraSqlServer(DTOFiltroEquipo filtro){
        super(filtro);
    }
    
    
    @Override
    protected String sqlInsertar() {
        return "exec [dbo].[VALIDADORA_INSERT] @Id=?, @Codigo=?, @NumeroDeSerie=?, @Marca=?, @Modelo=?, @PrecioDeCompra=?, @FechaDeAlta=?, @Descripcion=?, @MesesGarantia=?, @EsPrestamo=?, @NumeroPrecinto=?, @Estado=?, @IdUsuario=?, @IdLugarPropietario=?;";
    }
    
    @Override
    protected ArrayList parametrosInsertar() {
        ArrayList parametros = new ArrayList();
        Validadora v = this.getValidadora();
        if (v != null){
            parametros.add(v.getId());
            parametros.add(v.getCodigo());
            parametros.add(v.getNumeroDeSerie());
            parametros.add(v.getMarca());
            parametros.add(v.getModelo());
            parametros.add(v.getPrecioDeCompra());
            parametros.add(v.getFechaAlta());
            parametros.add(v.getDescripcion());
            parametros.add(v.getMesesGarantia());
            parametros.add(v.getEsPrestamo());
            parametros.add(v.getNumeroPrecinto());
            parametros.add(v.getEstado().toString());
            parametros.add(v.getUsuario().getId());
            parametros.add(v.getLugar().getId());
        }
        return parametros;
    }
    
    @Override
    protected String sqlActualizar() {
        return "exec [dbo].[VALIDADORA_UPDATE] @Id=?, @Codigo=?, @NumeroDeSerie=?, @Marca=?, @Modelo=?, @PrecioDeCompra=?, @FechaDeAlta=?, @Descripcion=?, @MesesGarantia=?, @EsPrestamo=?, @NumeroPrecinto=?, @Estado=?, @IdUsuario=?, @IdLugarPropietario=?, @IdEquipoPrestamo=?;";
    }
    
    @Override
    protected ArrayList parametrosActualizar() {
        ArrayList parametros = new ArrayList();
        Validadora v = this.getValidadora();
        if (v != null){
            parametros.add(v.getId());
            parametros.add(v.getCodigo());
            parametros.add(v.getNumeroDeSerie());
            parametros.add(v.getMarca());
            parametros.add(v.getModelo());
            parametros.add(v.getPrecioDeCompra());
            parametros.add(v.getFechaAlta());
            parametros.add(v.getDescripcion());
            parametros.add(v.getMesesGarantia());
            parametros.add(v.getEsPrestamo());
            parametros.add(v.getNumeroPrecinto());
            parametros.add(v.getEstado().toString());
            parametros.add(v.getUsuario().getId());
            parametros.add(v.getLugar().getId());
            parametros.add((v.getEquipoPrestamo() == null) ? null : v.getEquipoPrestamo().getId());
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
        Validadora v = this.getValidadora();
        if (v != null){
            parametros.add(EstadosEquipos.BAJA.toString());
            parametros.add(v.getId());
        }
        return parametros;
    }
    
    @Override
    protected String sqlBuscar() {
        String sql = "SELECT * FROM VValidadoras";
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
            if(this.getValidadora().getId() != null)
                condicion += "Id = ?";
            if(this.getValidadora().getCodigo() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " Codigo = ?";
            }
            if(this.getValidadora().getNumeroDeSerie() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " NumeroDeSerie = ?";
            }
            if(this.getValidadora().getMarca() != null) {
                if(condicion.endsWith("?"))
                    condicion += " AND";
                condicion += " Marca = ?";
            }
            if(this.getValidadora().getModelo() != null) {
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
            if(this.getValidadora().getId() != null)
                parametros.add(this.getValidadora().getId());
            if(this.getValidadora().getCodigo() != null)
                parametros.add(this.getValidadora().getCodigo());
            if(this.getValidadora().getNumeroDeSerie() != null)
                parametros.add(this.getValidadora().getNumeroDeSerie());
            if(this.getValidadora().getMarca() != null)
                parametros.add(this.getValidadora().getMarca());
            if(this.getValidadora().getModelo() != null)
                parametros.add(this.getValidadora().getModelo());
        }
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<Validadora> validadoras = new ArrayList();
        Validadora validadora;
        try {
            while(rs.next()) {
                validadora = new Validadora();
                validadora.setId(rs.getInt("Id"));
                if(rs.wasNull())
                    validadora.setId(null);
                validadora.setCodigo(rs.getString("Codigo"));
                validadora.setNumeroDeSerie(rs.getString("NumeroDeSerie"));
                validadora.setMarca(rs.getString("Marca"));
                validadora.setModelo(rs.getString("Modelo"));
                validadora.setPrecioDeCompra(rs.getDouble("PrecioDeCompra"));
                validadora.setFechaAlta(rs.getTimestamp("FechaDeAlta"));
                validadora.setDescripcion(rs.getString("Descripcion"));
                validadora.setMesesGarantia(rs.getInt("MesesGarantia"));
                if(rs.wasNull())
                    validadora.setMesesGarantia(null);
                validadora.setEsPrestamo(rs.getBoolean("EsPrestamo"));
                validadora.setNumeroPrecinto(rs.getInt("NumeroPrecinto"));
                if(rs.wasNull())
                    validadora.setNumeroPrecinto(null);
                validadora.setEstado(EstadosEquipos.valueOf(rs.getString("Estado")));
                validadora.getUsuario().setId(rs.getInt("IdUsuario"));
                if(rs.wasNull())
                    validadora.getUsuario().setId(null);
                validadora.getUsuario().setUsuario(rs.getString("Usuario"));
                validadora.setIdLugar(rs.getInt("IdLugarPropietario"));
                if(rs.wasNull())
                    validadora.setIdLugar(null);
                validadora.setIdEquipoPrestamo(rs.getInt("IdEquipoPrestamo"));
                if(rs.wasNull())
                    validadora.setIdEquipoPrestamo(null);
                validadora.setNombreLugarActual(rs.getString("LugarActual"));
                validadoras.add(validadora);
            }
            if(validadoras.size() == 1) {
                validadora = validadoras.get(0);
                this.getValidadora().setId(validadora.getId());
                this.getValidadora().setCodigo(validadora.getCodigo());
                this.getValidadora().setNumeroDeSerie(validadora.getNumeroDeSerie());
                this.getValidadora().setMarca(validadora.getMarca());
                this.getValidadora().setModelo(validadora.getModelo());
                this.getValidadora().setPrecioDeCompra(validadora.getPrecioDeCompra());
                this.getValidadora().setFechaAlta(validadora.getFechaAlta());
                this.getValidadora().setDescripcion(validadora.getDescripcion());
                this.getValidadora().setMesesGarantia(validadora.getMesesGarantia());
                this.getValidadora().setEsPrestamo(validadora.getEsPrestamo());
                this.getValidadora().setNumeroPrecinto(validadora.getNumeroPrecinto());
                this.getValidadora().setEstado(validadora.getEstado());
                this.getValidadora().setUsuario(validadora.getUsuario());
                this.getValidadora().setIdLugar(validadora.getIdLugar());
                this.getValidadora().setIdEquipoPrestamo(validadora.getIdEquipoPrestamo());
                this.getValidadora().setNombreLugarActual(validadora.getNombreLugarActual());
                validadoras.set(0, this.getValidadora());
            }
            else {
                this.setValidadora(null);
            }
        }
        catch(SQLException e) {
            validadoras.clear();
            this.setValidadora(null);
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
        return validadoras;
    }
    
}
