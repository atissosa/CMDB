package com.atissosa.cmdb_backend.domain;

import java.util.Date;

public class Validadora extends Equipo {
    
    public Validadora(String codigo, String numeroDeSerie, String marca, String modelo, Double precioDeCompra, Date fechaAlta, String descripcion, Integer mesesGarantia, Boolean esPrestamo, Integer numeroPrecinto, Lugar lugarPropietario, Usuario usuario) {
        super(codigo, numeroDeSerie, marca, modelo, precioDeCompra, fechaAlta, descripcion, mesesGarantia, esPrestamo, numeroPrecinto, lugarPropietario, usuario);
    }

    public Validadora() {
    }
    
    @Override
    public String getTipo(){ return "VALIDADORA"; }
    
    @Override
    public void modificarEquipo(Equipo e) {
        this.modificarDatosEquipo(e);
    }
    
    @Override
    public Boolean esConfigurable() {
        return false;
    }
}
