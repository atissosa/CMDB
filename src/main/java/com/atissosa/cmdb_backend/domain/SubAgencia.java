package com.atissosa.cmdb_backend.domain;

public class SubAgencia extends Lugar {

    private Integer numero;

    public Integer getNumero() {
        return this.numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    @Override
    public String getTipo() {
        return "SUBAGENCIA";
    }

}
