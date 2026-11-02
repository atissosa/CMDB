package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorMarcas extends Mapeador{
    
    protected MapeadorMarcas(){
    }   
    
    public static MapeadorMarcas nuevoMapeador() {
        MapeadorMarcas mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorMarcasSqlServer();
                break;
        }
        return mapeador;
    }

    @Override
    public Object getObjeto() {
        return null;
    }
}
