package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.Lugar;

public class DTOLugar {
    
    private Integer id;
    private String nombre;
    private Boolean activo;
    private String textoAmostrar;
    
    
    public DTOLugar(Lugar lugar) {
        this.id = lugar.getId();
        this.nombre = lugar.getNombre();
        this.activo = lugar.getActivo();
    }

    public DTOLugar() {}

    public String getTextoAmostrar() {
        return textoAmostrar;
    }

    public void setTextoAmostrar(String textoAmostrar) {
        this.textoAmostrar = textoAmostrar;
    }
    
    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNombre() { return this.nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Boolean getActivo() { return this.activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
}
