package com.atissosa.cmdb_backend.domain;

import java.util.Date;

public class Incidente {
    
    public enum EstadosIncidente {
        ABIERTO,
        EN_ESPERA,
        CERRADO
    }
    
    public enum TiposIncidente {
        HARDWARE,
        SOFTWARE
    }
    
    
    private Integer id;
    private Date fecha;
    private EstadosIncidente estado;
    private TiposIncidente tipo;
    
    
    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }
    
    public Date getFecha() { return this.fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    
    public EstadosIncidente getEstado() { return this.estado; }
    public void setEstado(EstadosIncidente estado) { this.estado = estado; }
    
    public TiposIncidente getTipo() { return this.tipo; }
    public void setTipo(TiposIncidente tipo) { this.tipo = tipo; }
    
}
