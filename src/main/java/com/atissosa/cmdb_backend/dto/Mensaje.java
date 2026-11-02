package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.dto.Respuesta.Tipos;

public class Mensaje {
    
    private Tipos tipo;
    private String texto;
    
    
    public Mensaje(Tipos tipo, String texto) {
        this.tipo = tipo;
        this.texto = texto;
    }
    
    
    public Tipos getTipo() { return this.tipo; }
    
    public String getTexto() { return this.texto; }
    
}
