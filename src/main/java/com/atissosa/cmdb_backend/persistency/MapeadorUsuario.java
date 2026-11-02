package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Usuario;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorUsuario extends Mapeador {
    
    private Usuario usuario;
    
    protected MapeadorUsuario(Usuario usuario){
        if(usuario == null)
            usuario = new Usuario();
        this.usuario = usuario;
    }
    
    
    protected Usuario getUsuario() { return this.usuario; }
    protected void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    
    public static MapeadorUsuario nuevoMapeador(Usuario usuario) {
        MapeadorUsuario mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorUsuarioSqlServer(usuario);
                break;
        }
        return mapeador;
    }

    @Override
    public Object getObjeto() {
        return this.usuario;
    }
    
}
