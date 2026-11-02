package com.atissosa.cmdb_backend.domain;

import java.util.Date;

public class OtroEquipo extends Equipo {

    public OtroEquipo() {
    }
    
    public enum TiposEquipo {
        MONITOR,
        SWITCH,
        SCANNER,
        LECTORA_CHEQUES_TARJETAS,
        LECTORA_BANDA_MAGNETICA,
        ROUTER,
        MODEM
    }
    
    
    private TiposEquipo tipo;
    
    public OtroEquipo(TiposEquipo tipo, String codigo, String numeroDeSerie, String marca, String modelo, Double precioDeCompra, Date fechaAlta, String descripcion, Integer mesesGarantia, Boolean esPrestamo, Integer numeroPrecinto, Lugar lugarPropietario, Usuario usuario) {
        super(codigo, numeroDeSerie, marca, modelo, precioDeCompra, fechaAlta, descripcion, mesesGarantia, esPrestamo, numeroPrecinto, lugarPropietario, usuario);
        this.tipo = tipo;
    }
    
    @Override
    public String getTipo(){ return this.tipo.toString(); }
    public void setTipo(TiposEquipo tipo) { this.tipo = tipo; }
    
    @Override
    public void modificarEquipo(Equipo e) {
        this.modificarDatosEquipo(e);
        this.tipo = TiposEquipo.valueOf(e.getTipo());
    }
    
    @Override
    public Boolean esConfigurable() {
        return this.getTipo().equals(TiposEquipo.ROUTER.toString());
    }
    
}
