package com.atissosa.cmdb_backend.domain;

public class PlantillaOtroEquipo extends PlantillaEquipo{
    
    private OtroEquipo.TiposEquipo tipo;
    
    public PlantillaOtroEquipo() {
        super();
    }

    public OtroEquipo.TiposEquipo getTipo() {
        return tipo;
    }

    public void setTipo(OtroEquipo.TiposEquipo tipo) {
        this.tipo = tipo;
    }
    
}
