package com.atissosa.cmdb_backend.domain;

import java.util.Date;

public class Impresora extends Equipo {

    public Impresora() {
    }
    
    public enum OpcionesImpresora {
        LASER_COLOR,
        LASER_MONOCROMO,
        CHORRO_TINTA_COLOR,
        CHORRO_TINTA_MONOCROMO,
    }
    
    public enum ConexionesImpresora {
        USB,
        RED
    }
    
    public Impresora(OpcionesImpresora opcion, Boolean multifuncion, String ip, String nombreHost, ConexionesImpresora conexion, String codigo, String numeroDeSerie, String marca, String modelo, Double precioDeCompra, Date fechaAlta, String descripcion, Integer mesesGarantia, Boolean esPrestamo, Integer numeroPrecinto, Lugar lugarPropietario, Usuario usuario) {
        super(codigo, numeroDeSerie, marca, modelo, precioDeCompra, fechaAlta, descripcion, mesesGarantia, esPrestamo, numeroPrecinto, lugarPropietario, usuario);
        this.opcion = opcion;
        this.multifuncion = multifuncion;
        this.ip = ip;
        this.nombreHost = nombreHost;
        this.conexion = conexion;
    }
    
    private OpcionesImpresora opcion;
    private Boolean multifuncion;
    private String ip;
    private String nombreHost;
    private ConexionesImpresora conexion;
    
    
    public OpcionesImpresora getOpcion() { return this.opcion; }
    public void setOpcion(OpcionesImpresora opcion) { this.opcion = opcion; }
    
    public Boolean getMultifuncion() { return this.multifuncion; }
    public void setMultifuncion(Boolean multifuncion) { this.multifuncion = multifuncion; }
    
    public String getIp() { return this.ip; }
    public void setIp(String ip) { this.ip = ip; }
    
    public String getNombreHost() { return this.nombreHost; }
    public void setNombreHost(String nombreHost) { this.nombreHost = nombreHost; }
    
    public ConexionesImpresora getConexion() { return this.conexion; }
    public void setConexion(ConexionesImpresora conexion) { this.conexion = conexion; }
    
    @Override
    public String getTipo(){ return "IMPRESORA"; }

    @Override
    public void modificarEquipo(Equipo e) {
        this.modificarDatosEquipo(e);
        this.opcion = ((Impresora)e).getOpcion();
        this.multifuncion = ((Impresora)e).getMultifuncion();
        this.ip = ((Impresora)e).getIp();
        this.nombreHost = ((Impresora)e).getNombreHost();
        this.conexion = ((Impresora)e).getConexion();
    }
    
    @Override
    public Boolean esConfigurable() {
        return true;
    }
    
}
