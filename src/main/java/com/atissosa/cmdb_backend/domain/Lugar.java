package com.atissosa.cmdb_backend.domain;

public abstract class Lugar {
    
    private Integer id;
    private String nombre;
    private Boolean activo;
    
    
    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNombre() { return this.nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Boolean getActivo() { return this.activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Lugar(String nombre) {
        this.nombre = nombre;
        this.activo = true;
    }
    
    public Lugar(){ }
    
    public abstract String getTipo();
}
