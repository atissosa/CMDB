package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.PlantillaImpresora;

public class DTOPlantillaImpresora extends DTOPlantillaEquipo {
    private boolean multifuncion;
    private String conexion;
    private String opcion;
    
    public DTOPlantillaImpresora() {
    }

    public DTOPlantillaImpresora(PlantillaImpresora p) {
        super(p);
        this.multifuncion = p.isMultifuncion();
        this.conexion = p.getConexion().toString();
        this.opcion = p.getOpcion().toString();
    }

    public boolean isMultifuncion() {
        return multifuncion;
    }

    public void setMultifuncion(boolean multifuncion) {
        this.multifuncion = multifuncion;
    }

    public String getConexion() {
        return conexion;
    }

    public void setConexion(String conexion) {
        this.conexion = conexion;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }
    
}
