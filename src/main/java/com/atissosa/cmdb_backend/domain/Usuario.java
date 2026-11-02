package com.atissosa.cmdb_backend.domain;

public class Usuario {
    
    private Integer id;
    private String usuario;
    private String contrasena;
    private Boolean activo;
    private DepartamentoCentral departamento;
    private Rol rol;
    
    
    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getUsuario() { return this.usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public String getContrasena() { return this.contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    
    public Boolean getActivo() { return this.activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public DepartamentoCentral getDepartamento() { return this.departamento; }
    public void setDepartamento(DepartamentoCentral departamento) { this.departamento = departamento; }
    
    public Rol getRol() { return this.rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    @Override
    public boolean equals(Object o) {
        return this.usuario.equalsIgnoreCase(((Usuario) o).getUsuario());
    }
}
