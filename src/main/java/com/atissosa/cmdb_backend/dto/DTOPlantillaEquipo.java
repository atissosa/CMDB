package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.PlantillaEquipo;

public abstract class DTOPlantillaEquipo {

    private Integer id;
    private String marca;
    private String modelo;
    private double precioCompra;
    private Integer mesesGarantia;

    public DTOPlantillaEquipo() {
    }

    public DTOPlantillaEquipo(PlantillaEquipo p) {
        this.id = p.getId();
        this.marca = p.getMarca();
        this.modelo = p.getModelo();
        this.precioCompra = p.getPrecioCompra();
        this.mesesGarantia = p.getMesesGarantia();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Integer getMesesGarantia() {
        return mesesGarantia;
    }

    public void setMesesGarantia(Integer mesesGarantia) {
        this.mesesGarantia = mesesGarantia;
    }
}
