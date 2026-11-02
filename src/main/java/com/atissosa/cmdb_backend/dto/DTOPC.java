package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.PC;

public class DTOPC extends DTOEquipo {
    
    private String ip;
    private String nombreHost;
    private String procesador;
    private Integer memoriaRam;
    private Integer tamanoDisco;
    
    
    public DTOPC(PC pc) {
        super(pc);
        this.ip = pc.getIp();
        this.nombreHost = pc.getNombreHost();
        this.procesador = pc.getProcesador();
        this.memoriaRam = pc.getMemoriaRam();
        this.tamanoDisco = pc.getTamanoDisco();
    }

    public DTOPC() {
    }
    
    
    public String getIp() { return this.ip; }
    public void setIp(String ip) { this.ip = ip; }
    
    public String getNombreHost() { return this.nombreHost; }
    public void setNombreHost(String nombreHost) { this.nombreHost = nombreHost; }
    
    public String getProcesador() { return this.procesador; }
    public void setProcesador(String procesador) { this.procesador = procesador; }
    
    public Integer getMemoriaRam() { return this.memoriaRam; }
    public void setMemoriaRam(Integer memoriaRam) { this.memoriaRam = memoriaRam; }
    
    public Integer getTamanoDisco() { return this.tamanoDisco; }
    public void setTamanoDisco(Integer tamanoDisco) { this.tamanoDisco = tamanoDisco; }
    
}
