package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.DepartamentoCentral;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorDepartamentoCentral extends MapeadorLugar {
    
    private DepartamentoCentral departamento;
    
    protected MapeadorDepartamentoCentral(DepartamentoCentral departamento){
        if(departamento == null)
            departamento = new DepartamentoCentral();
        this.departamento = departamento;
    }
    
    
    protected DepartamentoCentral getDepartamento() { return this.departamento; }
    protected void setDepartamento(DepartamentoCentral departamento) { this.departamento = departamento; }
    
    
    public static MapeadorDepartamentoCentral nuevoMapeador(DepartamentoCentral departamento) {
        MapeadorDepartamentoCentral mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorDepartamentoCentralSqlServer(departamento);
                break;
        }
        return mapeador;
    }

    @Override
    public Object getObjeto() {
        return this.departamento;
    }
    
}
