package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Equipo;
import com.atissosa.cmdb_backend.domain.Impresora;
import com.atissosa.cmdb_backend.domain.OtroEquipo;
import com.atissosa.cmdb_backend.domain.PC;
import com.atissosa.cmdb_backend.domain.Validadora;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;

public abstract class MapeadorEquipo extends Mapeador {
    
    private DTOFiltroEquipo filtro;
    
    
    public static MapeadorEquipo nuevoMapeadorEquipo(Equipo equipo) {
        MapeadorEquipo mapeador = null;
        switch(equipo.getClass().getSimpleName()){
            case "Impresora":
                mapeador = MapeadorImpresora.nuevoMapeador((Impresora) equipo);
                break;
            case "OtroEquipo":
                mapeador = MapeadorOtroEquipo.nuevoMapeador((OtroEquipo) equipo);
                break;
            case "PC":
                mapeador = MapeadorPC.nuevoMapeador((PC) equipo);
                break;
            case "Validadora":
                mapeador = MapeadorValidadora.nuevoMapeador((Validadora) equipo);
                break;
        }
        return mapeador;
    }
    
    
    public Equipo getEquipo() { return (Equipo) this.getObjeto(); }
    
    public DTOFiltroEquipo getFiltro() { return this.filtro; }
    public void setFiltro(DTOFiltroEquipo filtro) { this.filtro = filtro; }
    
}
