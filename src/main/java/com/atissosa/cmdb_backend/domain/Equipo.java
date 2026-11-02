package com.atissosa.cmdb_backend.domain;

import java.util.ArrayList;
import java.util.Date;

public abstract class Equipo {

    public enum EstadosEquipos {
        ALTA,
        ACTIVO,
        BAJA,
        AVERIADO,
        PRESTAMO_DISPONIBLE,
        PRESTADO,
        EN_RECUPERACION,
        EN_REPARACION,
        INACTIVO
    }
    
    //la vida util 2018 para equipos informaticos segun https://emprendefx.com/vida-util-de-activos-fijos/
    //es de 4 años, por lo tanto lo pongo como un atributo del equipo
    public static Integer mesesDevidaUtil = 48;

    private Integer id;
    private String codigo;
    private String numeroDeSerie;
    private String marca;
    private String modelo;
    private Double precioDeCompra;
    private Date fechaAlta;
    private String descripcion;
    private Integer mesesGarantia;
    private Boolean esPrestamo;
    private Integer numeroPrecinto;
    private ArrayList<Programa> programas;
    private EstadosEquipos estado;
    private Lugar lugarPropietario;
    private Integer idLugar;
    private ArrayList<ItemHistoricoLugar> historiaLugares;
    private ArrayList<Incidente> incidentes;
    private Usuario usuario;
    private Equipo equipoPrestamo;
    private Integer idEquipoPrestamo;
    private String nombreLugarActual;

    public Equipo() {
        this.programas = new ArrayList();
        this.historiaLugares = new ArrayList();
        this.incidentes = new ArrayList();
        this.usuario = new Usuario();
    }

    public Equipo(String codigo, String numeroDeSerie, String marca, String modelo, Double precioDeCompra, Date fechaAlta, String descripcion, Integer mesesGarantia, Boolean esPrestamo, Integer numeroPrecinto, Lugar lugarPropietario, Usuario usuario) {
        this.codigo = codigo;
        this.numeroDeSerie = numeroDeSerie;
        this.marca = marca;
        this.modelo = modelo;
        this.precioDeCompra = precioDeCompra;
        this.fechaAlta = fechaAlta;
        this.descripcion = descripcion;
        this.mesesGarantia = mesesGarantia;
        this.esPrestamo = esPrestamo;
        this.numeroPrecinto = numeroPrecinto;
        this.lugarPropietario = lugarPropietario;
        this.usuario = usuario;
        this.estado = EstadosEquipos.ALTA;
        this.programas = new ArrayList();
        this.historiaLugares = new ArrayList();
        this.incidentes = new ArrayList();
        this.idLugar = lugarPropietario.getId();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNumeroDeSerie() {
        return this.numeroDeSerie;
    }

    public void setNumeroDeSerie(String numeroDeSerie) {
        this.numeroDeSerie = numeroDeSerie;
    }

    public String getMarca() {
        return this.marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return this.modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Double getPrecioDeCompra() {
        return this.precioDeCompra;
    }

    public void setPrecioDeCompra(Double precioDeCompra) {
        this.precioDeCompra = precioDeCompra;
    }

    public Date getFechaAlta() {
        return this.fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getMesesGarantia() {
        return this.mesesGarantia;
    }

    public void setMesesGarantia(Integer mesesGarantia) {
        this.mesesGarantia = mesesGarantia;
    }

    public Boolean getEsPrestamo() {
        return this.esPrestamo;
    }

    public void setEsPrestamo(Boolean esPrestamo) {
        this.esPrestamo = esPrestamo;
    }

    public Integer getNumeroPrecinto() {
        return this.numeroPrecinto;
    }

    public void setNumeroPrecinto(Integer numeroPrecinto) {
        this.numeroPrecinto = numeroPrecinto;
    }

    public ArrayList<Programa> getProgramas() {
        return this.programas;
    }

    public Integer getIdLugar() {
        return idLugar;
    }

    public void setIdLugar(Integer idLugar) {
        this.idLugar = idLugar;
    }

    public EstadosEquipos getEstado() {
        return this.estado;
    }

    public void setEstado(EstadosEquipos estado) {
        this.estado = estado;
    }

    public Lugar getLugar() {
        return this.lugarPropietario;
    }

    public void setLugar(Lugar lugarPropietario) {
        this.lugarPropietario = lugarPropietario;
        if(this.lugarPropietario != null)
            this.idLugar = this.lugarPropietario.getId();
    }

    public ArrayList<ItemHistoricoLugar> getHistoriaLugares() {
        return this.historiaLugares;
    }
    
    public void setHistoriaLugares(ArrayList<ItemHistoricoLugar> lugares) {
        this.historiaLugares = lugares;
    }

    public ArrayList<Incidente> getIncidentes() {
        return this.incidentes;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombreLugarActual() {
        return this.nombreLugarActual;
    }

    public void setNombreLugarActual(String nombreLugarActual) {
        this.nombreLugarActual = nombreLugarActual;
    }

    public abstract String getTipo();
    
    public Lugar getLugarActual() {
        Lugar actual = null;
        int cantidad = 0;
        if(this.historiaLugares != null)
            cantidad = this.historiaLugares.size();
        if(cantidad > 0)
            actual = this.historiaLugares.get(cantidad - 1).getLugar();
        return actual;
    }
    
    public Equipo getEquipoPrestamo() { return this.equipoPrestamo; }
    public void setEquipoPrestamo(Equipo equipo) {
        this.equipoPrestamo = equipo;
        if(this.equipoPrestamo != null)
            this.idEquipoPrestamo = this.equipoPrestamo.getId();
    }
    
    public Integer getIdEquipoPrestamo() { return this.idEquipoPrestamo; }
    public void setIdEquipoPrestamo(Integer idEquipoPrestamo) { this.idEquipoPrestamo = idEquipoPrestamo; }

    protected void modificarDatosEquipo(Equipo e){
        this.codigo = e.getCodigo();
        this.numeroDeSerie = e.getNumeroDeSerie();
        this.marca = e.getMarca();
        this.modelo = e.getModelo();
        this.precioDeCompra = e.getPrecioDeCompra();
        this.fechaAlta = e.getFechaAlta();
        this.descripcion = e.getDescripcion();
        this.mesesGarantia = e.mesesGarantia;
        this.esPrestamo = e.getEsPrestamo();
        this.numeroPrecinto = e.getNumeroPrecinto();
        this.setLugar(e.getLugar());
        this.usuario = e.getUsuario();
        this.estado = e.getEstado();
    }
    
    public abstract void modificarEquipo(Equipo e);
    
    public abstract Boolean esConfigurable();
    
}
