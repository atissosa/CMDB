package com.atissosa.cmdb_backend.domain;

public class Service extends Lugar {

    private String descripcion;
    private String direccion;

    public Service(String nombre, String descripcion, String direccion) {
        super(nombre);
        this.descripcion = descripcion;
        this.direccion = direccion;
    }

    public Service() {
        super();
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String getTipo() {
        return "SERVICE";
    }

}
