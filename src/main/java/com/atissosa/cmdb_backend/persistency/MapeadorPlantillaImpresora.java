package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.PlantillaImpresora;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorPlantillaImpresora extends MapeadorPlantillaEquipo{

    private PlantillaImpresora plantillaImpresora;
    
    protected MapeadorPlantillaImpresora(PlantillaImpresora plantilla){
        if(plantilla == null)
            plantilla = new PlantillaImpresora();
        this.plantillaImpresora = plantilla;
    }
    
    public static MapeadorPlantillaImpresora nuevoMapeador(PlantillaImpresora plantillaImpresora) {
        MapeadorPlantillaImpresora mapeador = null;
        switch (Propiedades.BD_PROVEEDOR) {
            case MSSQLSERVER:
                mapeador = new MapeadorPlantillaImpresoraSqlServer(plantillaImpresora);
                break;
        }
        return mapeador;
    }

    public PlantillaImpresora getPlantillaImpresora() {
        return plantillaImpresora;
    }

    public void setPlantillaImpresora(PlantillaImpresora plantillaImpresora) {
        this.plantillaImpresora = plantillaImpresora;
    }
    
    @Override
    public Object getObjeto() {
        return this.plantillaImpresora;
    }
}
