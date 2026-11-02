package com.atissosa.cmdb_backend.domain;

public class DepartamentoCentral extends Lugar {

    private String descripcion;

    public DepartamentoCentral(String nombre, String descripcion) {
        super(nombre);
        this.descripcion = descripcion;
    }

    public DepartamentoCentral() {
        super();
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String getTipo() {
        return "DEPARTAMENTOCENTRAL";
    }

}
