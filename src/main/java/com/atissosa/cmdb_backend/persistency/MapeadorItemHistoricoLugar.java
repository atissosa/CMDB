package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.ItemHistoricoLugar;
import com.atissosa.cmdb_backend.global.Propiedades;

public abstract class MapeadorItemHistoricoLugar extends Mapeador {
    
    private ItemHistoricoLugar item;
    
    protected MapeadorItemHistoricoLugar(ItemHistoricoLugar item){
        if(item == null)
            item = new ItemHistoricoLugar();
        this.item = item;
    }
    
    
    protected ItemHistoricoLugar getItem() { return this.item; }
    protected void setItem(ItemHistoricoLugar item) { this.item = item; }
    
    
    public static MapeadorItemHistoricoLugar nuevoMapeador(ItemHistoricoLugar item) {
        MapeadorItemHistoricoLugar mapeador = null;
        switch(Propiedades.BD_PROVEEDOR){
            case MSSQLSERVER:
                mapeador = new MapeadorItemHistoricoLugarSqlServer(item);
                break;
        }
        return mapeador;
    }

    @Override
    public Object getObjeto() {
        return this.item;
    }
    
}
