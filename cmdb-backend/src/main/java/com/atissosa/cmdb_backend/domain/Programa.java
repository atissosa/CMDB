package com.atissosa.cmdb_backend.domain;

public class Programa {
    
    private Integer id;
    private String nombre;
    private String descripcion;
    
    
    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNombre() { return this.nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return this.descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
}
