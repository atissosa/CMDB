package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.PlantillaEquipo;
import com.atissosa.cmdb_backend.domain.PlantillaImpresora;
import com.atissosa.cmdb_backend.domain.PlantillaOtroEquipo;
import com.atissosa.cmdb_backend.domain.PlantillaPc;
import com.atissosa.cmdb_backend.domain.PlantillaValidadora;

public abstract class MapeadorPlantillaEquipo extends Mapeador{
    
    public static MapeadorPlantillaEquipo nuevoMapeadorPlantillaEquipo(PlantillaEquipo plantilla) {
        MapeadorPlantillaEquipo mapeador = null;
        switch(plantilla.getClass().getSimpleName()){
            case "PlantillaPc":
                mapeador = MapeadorPlantillaPc.nuevoMapeador((PlantillaPc) plantilla);
                break;
            case "PlantillaImpresora":
                mapeador = MapeadorPlantillaImpresora.nuevoMapeador((PlantillaImpresora) plantilla);
                break;
            case "PlantillaValidadora":
                mapeador = MapeadorPlantillaValidadora.nuevoMapeador((PlantillaValidadora) plantilla);
                break;
            case "PlantillaOtroEquipo":
                mapeador = MapeadorPlantillaOtroEquipo.nuevoMapeador((PlantillaOtroEquipo) plantilla);
                break;
        }
        return mapeador;
    }
    
    public static String obtenerMarcas(){
        return ";";
    }
}
