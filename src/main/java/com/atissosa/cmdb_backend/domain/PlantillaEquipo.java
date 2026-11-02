package com.atissosa.cmdb_backend.domain;

public abstract class PlantillaEquipo {
    private Integer id;
    private String marca;
    private String modelo;
    private double precioCompra;
    private Integer mesesGarantia;

    public PlantillaEquipo(String marca, String modelo, double precioCompra, Integer mesesGarantia) {
        this.marca = marca;
        this.modelo = modelo;
        this.precioCompra = precioCompra;
        this.mesesGarantia = mesesGarantia;
    }

    public PlantillaEquipo() {
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
