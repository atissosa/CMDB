package com.atissosa.cmdb_backend.domain;

public class PlantillaImpresora extends PlantillaEquipo {

    private boolean multifuncion;
    private Impresora.ConexionesImpresora conexion;
    private Impresora.OpcionesImpresora opcion;

    public PlantillaImpresora(boolean multifuncion, Impresora.ConexionesImpresora conexion, Impresora.OpcionesImpresora opcion, String marca, String modelo, double precioCompra, Integer mesesGarantia) {
        super(marca, modelo, precioCompra, mesesGarantia);
        this.multifuncion = multifuncion;
        this.conexion = conexion;
        this.opcion = opcion;
    }

    public PlantillaImpresora() {
    }

    public boolean isMultifuncion() {
        return multifuncion;
    }

    public void setMultifuncion(boolean multifuncion) {
        this.multifuncion = multifuncion;
    }

    public Impresora.ConexionesImpresora getConexion() {
        return conexion;
    }

    public void setConexion(Impresora.ConexionesImpresora conexion) {
        this.conexion = conexion;
    }

    public Impresora.OpcionesImpresora getOpcion() {
        return opcion;
    }

    public void setOpcion(Impresora.OpcionesImpresora opcion) {
        this.opcion = opcion;
    }

}
