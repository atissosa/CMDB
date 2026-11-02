package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.SubAgencia;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorSubAgencia extends MapeadorLugar {
    
    private SubAgencia subagencia;
    
    protected MapeadorSubAgencia(SubAgencia subagencia){
        if(subagencia == null)
            subagencia = new SubAgencia();
        this.subagencia = subagencia;
    }
    
    
    protected SubAgencia getSubagencia() { return this.subagencia; }
    protected void setSubagencia(SubAgencia subagencia) { this.subagencia = subagencia; }
    
    
    public static MapeadorSubAgencia nuevoMapeador(SubAgencia subagencia) {
        MapeadorSubAgencia mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorSubAgenciaSqlServer(subagencia);
                break;
        }
        return mapeador;
    }

    @Override
    public Object getObjeto() {
        return this.subagencia;
    }
    
}
