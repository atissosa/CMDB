
package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.PlantillaValidadora;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorPlantillaValidadora extends MapeadorPlantillaEquipo{
    private PlantillaValidadora plantillaValidadora;
    
    protected MapeadorPlantillaValidadora(PlantillaValidadora plantilla){
        if(plantilla == null)
            plantilla = new PlantillaValidadora();
        this.plantillaValidadora = plantilla;
    }
    
    public static MapeadorPlantillaValidadora nuevoMapeador(PlantillaValidadora plantillaValidadora) {
        MapeadorPlantillaValidadora mapeador = null;
        switch (Propiedades.BD_PROVEEDOR) {
            case MSSQLSERVER:
                mapeador = new MapeadorPlantillaValidadoraSqlServer(plantillaValidadora);
                break;
        }
        return mapeador;
    }

    public PlantillaValidadora getPlantillaValidadora() {
        return plantillaValidadora;
    }

    public void setPlantillaValidadora(PlantillaValidadora plantillaValidadora) {
        this.plantillaValidadora = plantillaValidadora;
    }
    
    @Override
    public Object getObjeto() {
        return this.plantillaValidadora;
    }
}
