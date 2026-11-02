
package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.PlantillaPc;

public class DTOPlantillaPc extends DTOPlantillaEquipo{
    private String procesador;
    private Integer memoriaRam;
    private Integer tamanoñoDisco;

    public DTOPlantillaPc() {
        super();
    }

    public DTOPlantillaPc(PlantillaPc p) {
        super(p);
        this.procesador = p.getProcesador();
        this.memoriaRam = p.getMemoriaRam();
        this.tamanoñoDisco = p.getTamanoñoDisco();
    }

    public String getProcesador() {
        return procesador;
    }

    public void setProcesador(String procesador) {
        this.procesador = procesador;
    }

    public Integer getMemoriaRam() {
        return memoriaRam;
    }

    public void setMemoriaRam(Integer memoriaRam) {
        this.memoriaRam = memoriaRam;
    }

    public Integer getTamanoñoDisco() {
        return tamanoñoDisco;
    }

    public void setTamanoñoDisco(Integer tamanoñoDisco) {
        this.tamanoñoDisco = tamanoñoDisco;
    }
    
    
}
