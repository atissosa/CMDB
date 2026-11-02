package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.PlantillaPc;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorPlantillaPc extends MapeadorPlantillaEquipo {

    private PlantillaPc plantillaPc;

    public MapeadorPlantillaPc() {
    }
    
    protected MapeadorPlantillaPc(PlantillaPc plantilla){
        if(plantilla == null)
            plantilla = new PlantillaPc();
        this.plantillaPc = plantilla;
    }
    
    static MapeadorPlantillaPc nuevoMapeador(PlantillaPc plantillaPc) {
        MapeadorPlantillaPc mapeador = null;
        switch (Propiedades.BD_PROVEEDOR) {
            case MSSQLSERVER:
                mapeador = new MapeadorPlantillaPcSqlServer(plantillaPc);
                break;
        }
        return mapeador;
    }

    public PlantillaPc getPlantillaPc() {
        return plantillaPc;
    }

    public void setPlantillaPc(PlantillaPc plantillaPc) {
        this.plantillaPc = plantillaPc;
    }
    
    @Override
    public Object getObjeto() {
        return this.plantillaPc;
    }

}
