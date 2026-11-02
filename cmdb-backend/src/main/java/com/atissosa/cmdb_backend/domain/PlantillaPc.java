package com.atissosa.cmdb_backend.domain;

public class PlantillaPc extends PlantillaEquipo {
    private String procesador;
    private Integer memoriaRam;
    private Integer tamanoñoDisco;

    public PlantillaPc(String procesador, Integer memoriaRam, Integer tamanoñoDisco, String marca, String modelo, double precioCompra, Integer mesesGarantia) {
        super(marca, modelo, precioCompra, mesesGarantia);
        this.procesador = procesador;
        this.memoriaRam = memoriaRam;
        this.tamanoñoDisco = tamanoñoDisco;
    }

    public PlantillaPc() {
        super();
    }

    public String getProcesador() {
        return procesador;
    }

    public void setProcesador(String procesador) {
        this.procesador = procesador;
    }

    public Integer getMemoriaRam() {
        return memoriaRam;
    }

    public void setMemoriaRam(Integer memoriaRam) {
        this.memoriaRam = memoriaRam;
    }

    public Integer getTamanoñoDisco() {
        return tamanoñoDisco;
    }

    public void setTamanoñoDisco(Integer tamanoñoDisco) {
        this.tamanoñoDisco = tamanoñoDisco;
    }
    
    
}
