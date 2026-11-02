package com.atissosa.cmdb_backend.errors;

public class ExcepcionConexion extends Exception {
    
    public ExcepcionConexion(String mensaje) {
        super(mensaje);
    }
    
    public ExcepcionConexion(String mensaje, Exception excepcionEncadenada) {
        super(mensaje, excepcionEncadenada);
    }
    
}
