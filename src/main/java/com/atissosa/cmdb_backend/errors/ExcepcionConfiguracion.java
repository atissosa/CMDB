package com.atissosa.cmdb_backend.errors;

public class ExcepcionConfiguracion extends Exception {
    
    public ExcepcionConfiguracion(String mensaje) {
        super(mensaje);
    }
    
    public ExcepcionConfiguracion(String mensaje, Exception excepcionEncadenada) {
        super(mensaje, excepcionEncadenada);
    }
    
}
