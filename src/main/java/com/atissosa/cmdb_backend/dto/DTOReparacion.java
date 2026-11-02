package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.Reparacion;

public class DTOReparacion extends DTOIncidente {
    
    private Double costo;
    private String diagnostico;
    private String resolucion;
    private DTOEquipo equipo;
    
    
    public DTOReparacion(Reparacion reparacion) {
        super(reparacion);
        this.costo = reparacion.getCosto();
        this.diagnostico = reparacion.getDiagnostico();
        this.resolucion = reparacion.getResolucion();
        //this.equipo = new DTOEquipo(reparacion.getEquipo());
    }
    
    
    public Double getCosto() { return this.costo; }
    public void setCosto(Double costo) { this.costo = costo; }
    
    public String getDiagnostico() { return this.diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    
    public String getResolucion() { return this.resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }
    
    public DTOEquipo getEquipo() { return this.equipo; }
    public void setEquipo(DTOEquipo equipo) { this.equipo = equipo; }
    
}
