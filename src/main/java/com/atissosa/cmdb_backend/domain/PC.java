package com.atissosa.cmdb_backend.domain;

import java.util.Date;

public class PC extends Equipo {
    
    private String ip;
    private String nombreHost;
    private String procesador;
    private Integer memoriaRam;
    private Integer tamanoDisco;
    
    public PC(String ip, String nombreHost, String procesador, Integer memoriaRam,
            Integer tamanoDisco, String codigo, String numeroDeSerie, String marca,
            String modelo, Double precioDeCompra, Date fechaAlta, String descripcion,
            Integer mesesGarantia, Boolean esPrestamo, Integer numeroPrecinto,
            Lugar lugarPropietario, Usuario usuario) {
        super(codigo, numeroDeSerie, marca, modelo, precioDeCompra, fechaAlta, 
                descripcion, mesesGarantia, esPrestamo, numeroPrecinto, lugarPropietario, usuario);
        this.ip = ip;
        this.nombreHost = nombreHost;
        this.procesador = procesador;
        this.memoriaRam = memoriaRam;
        this.tamanoDisco = tamanoDisco;
    }

    public PC() {
    }
    
    public String getIp() { return this.ip; }
    public void setIp(String ip) { this.ip = ip; }
    
    public String getNombreHost() { return this.nombreHost; }
    public void setNombreHost(String nombreHost) { this.nombreHost = nombreHost; }
    
    public String getProcesador() { return this.procesador; }
    public void setProcesador(String procesador) { this.procesador = procesador; }
    
    public Integer getMemoriaRam() { return this.memoriaRam; }
    public void setMemoriaRam(Integer memoriaRam) { this.memoriaRam = memoriaRam; }
    
    public Integer getTamanoDisco() { return this.tamanoDisco; }
    public void setTamanoDisco(Integer tamanoDisco) { this.tamanoDisco = tamanoDisco; }
    
    @Override
    public String getTipo(){ return "PC"; }
    
    @Override
    public void modificarEquipo(Equipo e) {
        this.modificarDatosEquipo(e);
        this.ip = ((PC)e).getIp();
        this.nombreHost = ((PC)e).getNombreHost();
        this.procesador = ((PC)e).getProcesador();
        this.memoriaRam = ((PC)e).getMemoriaRam();
        this.tamanoDisco = ((PC)e).getTamanoDisco();
    }
    
    @Override
    public Boolean esConfigurable() {
        return true;
    }
}
