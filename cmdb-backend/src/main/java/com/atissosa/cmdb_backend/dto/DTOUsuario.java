package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.Usuario;

public class DTOUsuario {
    
    private Integer id;
    private String usuario;
    private String rol;
    private String departamento;
    private Boolean activo;
    
    
    public DTOUsuario(Usuario usuario) {
        this.id = usuario.getId();
        this.usuario = usuario.getUsuario();
        this.rol = usuario.getRol().getNombre();
        this.departamento = usuario.getDepartamento().getNombre();
        this.activo = usuario.getActivo();
    }

    public DTOUsuario() {
    }
    
    
    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getUsuario() { return this.usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public String getRol() { return this.rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public String getDepartamento() { return this.departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    
    public Boolean getActivo() { return this.activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
}
