package com.atissosa.cmdb_backend.domain;

public class Session {
  private Usuario usuario;
  private ProcesoX contador;

    public Session(Usuario usuario, ProcesoX contador) {
        this.usuario = usuario;
        this.contador = contador;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public ProcesoX getContador() {
        return contador;
    }

    public void setContador(ProcesoX contador) {
        this.contador = contador;
    }
    
}
