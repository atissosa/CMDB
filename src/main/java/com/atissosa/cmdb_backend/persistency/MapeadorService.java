package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Service;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorService extends MapeadorLugar {
    
    private Service service;
    
    protected MapeadorService(Service service){
        if(service == null)
            service = new Service();
        this.service = service;
    }
    
    
    protected Service getService() { return this.service; }
    protected void setService(Service service) { this.service = service; }
    
    
    public static MapeadorService nuevoMapeador(Service service) {
        MapeadorService mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorServiceSqlServer(service);
                break;
        }
        return mapeador;
    }

    @Override
    public Object getObjeto() {
        return this.service;
    }
    
}
