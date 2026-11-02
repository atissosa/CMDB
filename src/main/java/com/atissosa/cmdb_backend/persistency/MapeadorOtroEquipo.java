package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.OtroEquipo;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorOtroEquipo extends MapeadorEquipo {
    
    private OtroEquipo otroEquipo;
    
    protected MapeadorOtroEquipo(OtroEquipo otroEquipo){
        if(otroEquipo == null)
            otroEquipo = new OtroEquipo();
        this.otroEquipo = otroEquipo;
    }
    
    protected MapeadorOtroEquipo(DTOFiltroEquipo filtro){
        this.otroEquipo = new OtroEquipo();
        if(filtro == null)
            filtro = new DTOFiltroEquipo();
        this.setFiltro(filtro);
    }
    
    
    protected OtroEquipo getOtroEquipo() { return this.otroEquipo; }
    protected void setOtroEquipo(OtroEquipo otroEquipo) { this.otroEquipo = otroEquipo; }
    
    
    public static MapeadorOtroEquipo nuevoMapeador(OtroEquipo otroEquipo) {
        MapeadorOtroEquipo mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorOtroEquipoSqlServer(otroEquipo);
                break;
        }
        return mapeador;
    }
    
    public static MapeadorOtroEquipo nuevoMapeador(DTOFiltroEquipo filtro) {
        MapeadorOtroEquipo mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorOtroEquipoSqlServer(filtro);
                break;
        }
        return mapeador;
    }
    

    @Override
    public Object getObjeto() {
        return this.otroEquipo;
    }
    
}
