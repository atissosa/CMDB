package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.Equipo;
import java.util.ArrayList;

public class DTOFiltroEquipo {
    private ArrayList<Integer> id;
    private ArrayList<String> codigo;
    private ArrayList<String> numeroSerie;
    private Boolean esPrestamo;
    private ArrayList<String> usuario;
    private ArrayList<String> tipo;
    private ArrayList<String> estado;
    private ArrayList<Integer> idLugar;
    
    
    public DTOFiltroEquipo(){
        this.codigo = new ArrayList();
        this.numeroSerie = new ArrayList();
        this.usuario = new ArrayList();
        this.tipo = new ArrayList();
        this.estado = new ArrayList();
        this.idLugar = new ArrayList();
    }

    
    public boolean tieneFiltros() {
        return this.tieneId() || this.tieneCodigo() || this.tieneNumeroSerie() || this.tieneEsPrestamo() || this.tieneUsuario()
                || this.tieneTipo() || this.tieneEstado() || this.tieneIdLugar();
    }
    
    public ArrayList<Integer> getId() { return this.id; }
    public void setId(ArrayList<Integer> id) { this.id = idLugar; }
    public void agregarId(Integer id) {
        if(this.id == null)
            this.id = new ArrayList();
        this.id.add(id);
    }
    public boolean tieneId() { return this.id != null && !this.id.isEmpty(); }
    
    public ArrayList<String> getCodigo() { return this.codigo; }
    public void setCodigo(ArrayList<String> codigo) {
        this.codigo = codigo;
        for(int i = 0; i < this.codigo.size(); i++){
            this.codigo.set(i, this.codigo.get(i).toLowerCase());
        }
    }
    public void agregarCodigo(String codigo) {
        if(this.codigo == null)
            this.codigo = new ArrayList();
        this.codigo.add(codigo.toLowerCase());
    }
    public boolean tieneCodigo() { return this.codigo != null && !this.codigo.isEmpty(); }
    
    public ArrayList<String> getNumeroSerie() { return this.numeroSerie; }
    public void setNumeroSerie(ArrayList<String> numeroSerie) {
        this.numeroSerie = numeroSerie;
        for(int i = 0; i < this.numeroSerie.size(); i++){
            this.numeroSerie.set(i, this.numeroSerie.get(i).toLowerCase());
        }
    }
    public void agregarNumeroSerie(String numeroSerie) {
        if(this.numeroSerie == null)
            this.numeroSerie = new ArrayList();
        this.numeroSerie.add(numeroSerie.toLowerCase());
    }
    public boolean tieneNumeroSerie() { return this.numeroSerie != null && !this.numeroSerie.isEmpty(); }

    public boolean getEsPrestamo() { return this.esPrestamo; }
    public void setEsPrestamo(Boolean esPrestamo) { this.esPrestamo = esPrestamo; }
    public boolean tieneEsPrestamo() { return this.esPrestamo != null; }
    
    public ArrayList<String> getUsuario() { return this.usuario; }
    public void setUsuario(ArrayList<String> usuario) {
        this.usuario = usuario;
        for(int i = 0; i < this.usuario.size(); i++){
            this.usuario.set(i, this.usuario.get(i).toLowerCase());
        }
    }
    public void agregarUsuario(String usuario) {
        if(this.usuario == null)
            this.usuario = new ArrayList();
        this.usuario.add(usuario.toLowerCase());
    }
    public boolean tieneUsuario() { return this.usuario != null && !this.usuario.isEmpty(); }
    
    public ArrayList<String> getTipo() { return this.tipo; }
    public void setTipo(ArrayList<String> tipo) {
        this.tipo = tipo;
        for(int i = 0; i < this.tipo.size(); i++){
            this.tipo.set(i, this.tipo.get(i).toLowerCase());
        }
    }
    public void agregarTipo(String tipo) {
        if(this.tipo == null)
            this.tipo = new ArrayList();
        this.tipo.add(tipo.toLowerCase());
    }
    public boolean tieneTipo() { return this.tipo != null && !this.tipo.isEmpty(); }
    
    public ArrayList<String> getEstado() { return this.estado; }
    public void setEstado(ArrayList<String> estado) {
        this.estado = estado;
        for(int i = 0; i < this.estado.size(); i++){
            this.estado.set(i, this.estado.get(i).toLowerCase());
        }
    }
    public void agregarEstado(String estado) {
        if(this.estado == null)
            this.estado = new ArrayList();
        this.estado.add(estado.toLowerCase());
    }
    public boolean tieneEstado() { return this.estado != null && !this.estado.isEmpty(); }
    
    public ArrayList<Integer> getIdLugar() { return this.idLugar; }
    public void setIdLugar(ArrayList<Integer> idLugar) { this.idLugar = idLugar; }
    public void agregarIdLugar(Integer idLugar) {
        if(this.idLugar == null)
            this.idLugar = new ArrayList();
        this.idLugar.add(idLugar);
    }
    public boolean tieneIdLugar() { return this.idLugar != null && !this.idLugar.isEmpty(); }
    
    
    public boolean comparar(Equipo equipo) {
        return !((this.tieneId() && !this.id.contains(equipo.getId()))
                || (this.tieneCodigo() && !this.codigo.contains(equipo.getCodigo().toLowerCase()))
                || (this.tieneNumeroSerie() && !this.numeroSerie.contains(equipo.getNumeroDeSerie().toLowerCase()))
                || (this.tieneEsPrestamo() && !equipo.getEsPrestamo().equals(this.esPrestamo))
                || (this.tieneUsuario() && !this.usuario.contains(equipo.getUsuario().getUsuario().toLowerCase()))
                || (this.tieneTipo() && !this.tipo.contains(equipo.getTipo().toLowerCase()))
                || (this.tieneEstado() && !this.estado.contains(equipo.getEstado().toString().toLowerCase()))
                || (this.tieneIdLugar() && !this.idLugar.contains(equipo.getLugar().getId())));
    }
    
}