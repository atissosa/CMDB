package com.atissosa.cmdb_backend.utilities;

public class Validador {
    public static boolean NombreValido(String nombre) {
        return !nombre.isEmpty();
    }
    
    public static boolean IpValida(String ip){
        return ip.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.)"
                + "{3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
    }
}
