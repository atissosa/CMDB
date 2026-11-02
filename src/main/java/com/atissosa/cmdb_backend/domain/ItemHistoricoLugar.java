package com.atissosa.cmdb_backend.domain;

import com.atissosa.cmdb_backend.domain.Equipo.EstadosEquipos;
import java.util.Date;

public class ItemHistoricoLugar {
    
    private Integer id;
    private Integer idEquipo;
    private Equipo equipo;
    private String descripcion;
    private Date fecha;
    private Integer idLugar;
    private Lugar lugar;
    private Usuario usuario;
    private EstadosEquipos estado;
    private Equipo equipoPrestamo;
    private Integer idEquipoPrestamo;
    
    
    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getIdEquipo() { return this.idEquipo; }
    public void setIdEquipo(Integer idEquipo) { this.idEquipo = idEquipo; }
    
    public Equipo getEquipo() { return this.equipo; }
    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
        if(equipo == null){
            this.idEquipo = null;
        }else{
            this.idEquipo = equipo.getId();
        }
    }
    
    public String getDescripcion() { return this.descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Date getFecha() { return this.fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    
    public Integer getIdLugar() { return this.idLugar; }
    public void setIdLugar(Integer idLugar) { this.idLugar = idLugar; }
    
    public Lugar getLugar() { return this.lugar; }
    public void setLugar(Lugar lugar) {
        this.lugar = lugar;
        if(lugar == null){
            this.idLugar = null;
        }else{
            this.idLugar = lugar.getId();
        }
    }
    
    public Usuario getUsuario() { return this.usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public EstadosEquipos getEstado() { return this.estado; }
    public void setEstado(EstadosEquipos estado) { this.estado = estado; }
    
    public Equipo getEquipoPrestamo() { return this.equipoPrestamo; }
    public void setEquipoPrestamo(Equipo equipo) {
        this.equipoPrestamo = equipo;
        if(equipo == null){
            this.idEquipoPrestamo = null;
        }else{
            this.idEquipoPrestamo = equipo.getId();
        }
    }
    
    public Integer getIdEquipoPrestamo() { return this.idEquipoPrestamo; }
    public void setIdEquipoPrestamo(Integer idEquipoPrestamo) { this.idEquipoPrestamo = idEquipoPrestamo; }
    
}
