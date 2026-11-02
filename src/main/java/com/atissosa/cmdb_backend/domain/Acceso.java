package com.atissosa.cmdb_backend.domain;

public class Acceso {
    
    private String operacion;
    private Rol rol;
    
    
    public String getOperacion() { return this.operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }
    
    public Rol getRol() { return this.rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    
}
