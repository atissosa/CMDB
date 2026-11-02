package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.util.ArrayList;

public abstract class Mapeador {
    
    protected abstract String sqlInsertar();
    
    protected abstract ArrayList parametrosInsertar();
    
    protected abstract String sqlActualizar();
    
    protected abstract ArrayList parametrosActualizar();
    
    protected abstract String sqlEliminar();
    
    protected abstract ArrayList parametrosEliminar();
    
    protected abstract String sqlBuscar();
    
    protected abstract ArrayList parametrosBuscar();

    public abstract ArrayList leer(ResultSet rs) throws ExcepcionConexion;

    public abstract Object getObjeto();
}
