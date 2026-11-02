package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.PC;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorPC extends MapeadorEquipo {
    
    private PC pc;
    
    protected MapeadorPC(PC pc){
        if(pc == null)
            pc = new PC();
        this.pc = pc;
    }
    
    protected MapeadorPC(DTOFiltroEquipo filtro){
        this.pc = new PC();
        if(filtro == null)
            filtro = new DTOFiltroEquipo();
        this.setFiltro(filtro);
    }
    
    
    protected PC getPC() { return this.pc; }
    protected void setPC(PC pc) { this.pc = pc; }
    
    
    public static MapeadorPC nuevoMapeador(PC pc) {
        MapeadorPC mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorPCSqlServer(pc);
                break;
        }
        return mapeador;
    }
    
    public static MapeadorPC nuevoMapeador(DTOFiltroEquipo filtro) {
        MapeadorPC mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorPCSqlServer(filtro);
                break;
        }
        return mapeador;
    }
    

    @Override
    public Object getObjeto() {
        return this.pc;
    }
    
}
