package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.Impresora;

public class DTOImpresora extends DTOEquipo {
    
    private String opcion;
    private Boolean multifuncion;
    private String ip;
    private String nombreHost;
    private String conexion;
    
    
    public DTOImpresora(Impresora impresora) {
        super(impresora);
        this.opcion = impresora.getOpcion().toString();
        this.multifuncion = impresora.getMultifuncion();
        this.ip = impresora.getIp();
        this.nombreHost = impresora.getNombreHost();
        this.conexion = impresora.getConexion().toString();
    }

    public DTOImpresora() {
    }
    
    
    public String getOpcion() { return this.opcion; }
    public void setOpcion(String opcion) { this.opcion = opcion; }
    
    public Boolean getMultifuncion() { return this.multifuncion; }
    public void setMultifuncion(Boolean multifuncion) { this.multifuncion = multifuncion; }
    
    public String getIp() { return this.ip; }
    public void setIp(String ip) { this.ip = ip; }
    
    public String getNombreHost() { return this.nombreHost; }
    public void setNombreHost(String nombreHost) { this.nombreHost = nombreHost; }
    
    public String getConexion() { return this.conexion; }
    public void setConexion(String conexion) { this.conexion = conexion; }
    
}
