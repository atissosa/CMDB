package com.atissosa.cmdb_backend.domain;

public class Reparacion extends Incidente {
    
    private Double costo;
    private String diagnostico;
    private String resolucion;
    private Equipo equipo;
    
    
    public Double getCosto() { return this.costo; }
    public void setCosto(Double costo) { this.costo = costo; }
    
    public String getDiagnostico() { return this.diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    
    public String getResolucion() { return this.resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }
    
    public Equipo getEquipo() { return this.equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }
    
}
