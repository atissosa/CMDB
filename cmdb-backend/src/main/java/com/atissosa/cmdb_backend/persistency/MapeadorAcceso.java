package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Acceso;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorAcceso extends Mapeador {
    
    private Acceso acceso;
    
    protected MapeadorAcceso(Acceso acceso){
        if(acceso == null)
            acceso = new Acceso();
        this.acceso = acceso;
    }
    
    
    protected Acceso getAcceso() { return this.acceso; }
    protected void setAcceso(Acceso acceso) { this.acceso = acceso; }
    
    
    public static MapeadorAcceso nuevoMapeador(Acceso acceso) {
        MapeadorAcceso mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorAccesoSqlServer(acceso);
                break;
        }
        return mapeador;
    }

    @Override
    public Object getObjeto() {
        return this.acceso;
    }
    
}
