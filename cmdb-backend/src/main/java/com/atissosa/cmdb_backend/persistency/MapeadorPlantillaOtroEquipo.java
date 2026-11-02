package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.PlantillaOtroEquipo;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorPlantillaOtroEquipo extends MapeadorPlantillaEquipo{
    private PlantillaOtroEquipo plantillaOtroEquipo;
    
    protected MapeadorPlantillaOtroEquipo(PlantillaOtroEquipo plantilla){
        if(plantilla == null)
            plantilla = new PlantillaOtroEquipo();
        this.plantillaOtroEquipo = plantilla;
    }
    
    public static MapeadorPlantillaOtroEquipo nuevoMapeador(PlantillaOtroEquipo plantillaOtroEquipo) {
        MapeadorPlantillaOtroEquipo mapeador = null;
        switch (Propiedades.BD_PROVEEDOR) {
            case MSSQLSERVER:
                mapeador = new MapeadorPlantillaOtroEquipoSqlServer(plantillaOtroEquipo);
                break;
        }
        return mapeador;
    }

    public PlantillaOtroEquipo getPlantillaOtroEquipo() {
        return plantillaOtroEquipo;
    }

    public void setPlantillaOtroEquipo(PlantillaOtroEquipo plantillaOtroEquipo) {
        this.plantillaOtroEquipo = plantillaOtroEquipo;
    }
    
    @Override
    public Object getObjeto() {
        return this.plantillaOtroEquipo;
    }
}
