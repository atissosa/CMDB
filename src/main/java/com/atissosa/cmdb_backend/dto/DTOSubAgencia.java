package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.SubAgencia;

public class DTOSubAgencia extends DTOLugar {
    
    private Integer numero;

    public DTOSubAgencia() {
    }
    
    
    public DTOSubAgencia(SubAgencia subagencia) {
        super(subagencia);
        this.numero = subagencia.getNumero();
        this.setTextoAmostrar(subagencia.getNumero() + " - " + subagencia.getNombre());
    }
    
    
    public Integer getNumero() { return this.numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    
}
