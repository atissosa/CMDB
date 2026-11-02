package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.Incidente;
import java.util.Date;

public class DTOIncidente {
    
    private Integer id;
    private Date fecha;
    private String estado;
    private String tipo;
    
    
    public DTOIncidente(Incidente incidente) {
        this.id = incidente.getId();
        this.fecha = incidente.getFecha();
        this.estado = incidente.getEstado().toString();
        this.tipo = incidente.getTipo().toString();
    }
    
    
    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }
    
    public Date getFecha() { return this.fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    
    public String getEstado() { return this.estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getTipo() { return this.tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
}
