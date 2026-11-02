package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.DepartamentoCentral;

public class DTODepartamentoCentral extends DTOLugar {
    
    
    private String descripcion;
    
    
    public DTODepartamentoCentral(DepartamentoCentral departamento) {
        super(departamento);
        this.descripcion = departamento.getDescripcion();
        this.setTextoAmostrar(departamento.getNombre());
    }

    public DTODepartamentoCentral() {}
    
    
    public String getDescripcion() { return this.descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
}
