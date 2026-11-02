package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Impresora;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorImpresora extends MapeadorEquipo {
    
    private Impresora impresora;
    
    protected MapeadorImpresora(Impresora impresora){
        if(impresora == null)
            impresora = new Impresora();
        this.impresora = impresora;
    }
    
    protected MapeadorImpresora(DTOFiltroEquipo filtro){
        this.impresora = new Impresora();
        if(filtro == null)
            filtro = new DTOFiltroEquipo();
        this.setFiltro(filtro);
    }
    
    
    protected Impresora getImpresora() { return this.impresora; }
    protected void setImpresora(Impresora impresora) { this.impresora = impresora; }
    
    
    public static MapeadorImpresora nuevoMapeador(Impresora impresora) {
        MapeadorImpresora mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorImpresoraSqlServer(impresora);
                break;
        }
        return mapeador;
    }
    
    public static MapeadorImpresora nuevoMapeador(DTOFiltroEquipo filtro) {
        MapeadorImpresora mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorImpresoraSqlServer(filtro);
                break;
        }
        return mapeador;
    }

    
    @Override
    public Object getObjeto() {
        return this.impresora;
    }
    
}
