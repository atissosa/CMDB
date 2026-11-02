package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.DepartamentoCentral;
import com.atissosa.cmdb_backend.domain.Lugar;
import com.atissosa.cmdb_backend.domain.Service;
import com.atissosa.cmdb_backend.domain.SubAgencia;

public abstract class MapeadorLugar extends Mapeador {
    
    public static MapeadorLugar nuevoMapeadorLugar(Lugar lugar) {
        MapeadorLugar mapeador = null;
        switch(lugar.getClass().getSimpleName()){
            case "DepartamentoCentral":
                mapeador = MapeadorDepartamentoCentral.nuevoMapeador((DepartamentoCentral) lugar);
                break;
            case "Service":
                mapeador = MapeadorService.nuevoMapeador((Service) lugar);
                break;
            case "SubAgencia":
                mapeador = MapeadorSubAgencia.nuevoMapeador((SubAgencia) lugar);
                break;
        }
        return mapeador;
    }
    
    
    public Lugar getLugar() {
        return (Lugar) this.getObjeto();
    }
    
}
