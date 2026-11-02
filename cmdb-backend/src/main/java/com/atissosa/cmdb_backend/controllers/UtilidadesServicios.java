package com.atissosa.cmdb_backend.controllers;

import com.atissosa.cmdb_backend.dto.Respuesta;
import java.util.Base64;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

public abstract class UtilidadesServicios {
    
    protected static Response nuevaResponse(Respuesta respuesta) {
        int estado = 0;
        switch(respuesta.getTipo()){
            case OK:
                estado = 200;
                break;
            case ERROR:
                estado = 500;
                break;
            case NO_AUTORIZADO:
                estado = 403;
                break;
            case NO_AUTENTICADO:
                estado = 401;
                break;
        }
        return Response.status(estado).entity(respuesta).build();
    }
    
    public static String[] obtenerUsuarioContraseña(HttpHeaders headers){
        String autorizacion = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        String[] credencialesSeparadas = { "", "" };
        if (autorizacion != null && autorizacion.startsWith("Basic")) {
            String credencialesCodificadas = autorizacion.substring("Basic".length()).trim();
            String credenciales = new String(Base64.getDecoder().decode(credencialesCodificadas));
            credencialesSeparadas = credenciales.split(":", 2);
        }
        return credencialesSeparadas;
    }
}
