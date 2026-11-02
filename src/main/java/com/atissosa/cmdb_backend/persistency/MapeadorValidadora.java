package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Validadora;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorValidadora extends MapeadorEquipo {
    
    private Validadora validadora;
    
    protected MapeadorValidadora(Validadora validadora){
        if(validadora == null)
            validadora = new Validadora();
        this.validadora = validadora;
    }
    
    protected MapeadorValidadora(DTOFiltroEquipo filtro){
        this.validadora = new Validadora();
        if(filtro == null)
            filtro = new DTOFiltroEquipo();
        this.setFiltro(filtro);
    }
    
    
    protected Validadora getValidadora() { return this.validadora; }
    protected void setValidadora(Validadora validadora) { this.validadora = validadora; }
    
    
    public static MapeadorValidadora nuevoMapeador(Validadora validadora) {
        MapeadorValidadora mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorValidadoraSqlServer(validadora);
                break;
        }
        return mapeador;
    }
    
    public static MapeadorValidadora nuevoMapeador(DTOFiltroEquipo filtro) {
        MapeadorValidadora mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorValidadoraSqlServer(filtro);
                break;
        }
        return mapeador;
    }
    

    @Override
    public Object getObjeto() {
        return this.validadora;
    }
    
}
