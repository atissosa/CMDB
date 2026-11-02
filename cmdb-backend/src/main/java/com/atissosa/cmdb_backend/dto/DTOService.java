package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.Service;

public class DTOService extends DTOLugar {
    
    private String descripcion;
    private String direccion;
    
    
    public DTOService(Service service) {
        super(service);
        this.descripcion = service.getDescripcion();
        this.direccion = service.getDireccion();
        this.setTextoAmostrar(service.getNombre());
    }
    
    public DTOService(){}
    
    public String getDescripcion() { return this.descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getDireccion() { return this.direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
}
