package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.ItemHistoricoLugar;
import java.text.SimpleDateFormat;

public class DTOItemHistoricoLugar {
    
    private Integer id;
    private Integer idEquipo;
    private String codigoEquipo;
    private String numeroSerieEquipo;
    private String descripcion;
    private String fecha;
    private Integer idLugar;
    private String nombreLugar;
    private String tipoLugar;
    private String usuario;
    private String estado;
    private Integer idEquipoPrestamo;
    private String codigoEquipoPrestamo;
    private String numeroSerieEquipoPrestamo;
    
    
    public DTOItemHistoricoLugar() {}
    
    public DTOItemHistoricoLugar(ItemHistoricoLugar item) {
        this.id = item.getId();
        if(item.getEquipo() != null){
            this.idEquipo = item.getEquipo().getId();
            this.codigoEquipo = item.getEquipo().getCodigo();
            this.numeroSerieEquipo = item.getEquipo().getNumeroDeSerie();
        }
        this.descripcion = item.getDescripcion();
        this.fecha = (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS")).format(item.getFecha());
        if(item.getLugar() != null){
            this.idLugar = item.getLugar().getId();
            this.nombreLugar = item.getLugar().getNombre();
            this.tipoLugar = item.getLugar().getTipo();
        }
        if(item.getUsuario() != null){
            this.usuario = item.getUsuario().getUsuario();
        }
        if(item.getEstado() != null){
            this.estado = item.getEstado().toString();
        }
        if(item.getEquipoPrestamo() != null){
            this.idEquipoPrestamo = item.getEquipoPrestamo().getId();
            this.codigoEquipoPrestamo = item.getEquipoPrestamo().getCodigo();
            this.numeroSerieEquipoPrestamo = item.getEquipoPrestamo().getNumeroDeSerie();
        }
    }
    
    
    public Integer getId(){ return this.id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getIdEquipo(){ return this.idEquipo; }
    public void setIdEquipo(Integer idEquipo) { this.idEquipo = idEquipo; }
    
    public String getCodigoEquipo(){ return this.codigoEquipo; }
    public void setCodigoEquipo(String codigoEquipo) { this.codigoEquipo = codigoEquipo; }
    
    public String getNumeroSerieEquipo(){ return this.numeroSerieEquipo; }
    public void setNumeroSerieEquipo(String numeroSerieEquipo) { this.numeroSerieEquipo = numeroSerieEquipo; }
    
    public String getDescripcion(){ return this.descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getFecha(){ return this.fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    
    public Integer getIdLugar(){ return this.idLugar; }
    public void setIdLugar(Integer idLugar) { this.idLugar = idLugar; }
    
    public String getNombreLugar(){ return this.nombreLugar; }
    public void setNombreLugar(String nombreLugar) { this.nombreLugar = nombreLugar; }
    
    public String getTipoLugar(){ return this.tipoLugar; }
    public void setTipoLugar(String tipoLugar) { this.tipoLugar = tipoLugar; }
    
    public String getUsuario(){ return this.usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public String getEstado(){ return this.estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public Integer getIdEquipoPrestamo(){ return this.idEquipoPrestamo; }
    public void setIdEquipoPrestamo(Integer idEquipoPrestamo) { this.idEquipoPrestamo = idEquipoPrestamo; }
    
    public String getCodigoEquipoPrestamo(){ return this.codigoEquipoPrestamo; }
    public void setCodigoEquipoPrestamo(String codigoEquipoPrestamo) { this.codigoEquipoPrestamo = codigoEquipoPrestamo; }
    
    public String getNumeroSerieEquipoPrestamo(){ return this.numeroSerieEquipoPrestamo; }
    public void setNumeroSerieEquipoPrestamo(String numeroSerieEquipoPrestamo) { this.numeroSerieEquipoPrestamo = numeroSerieEquipoPrestamo; }
    
}
