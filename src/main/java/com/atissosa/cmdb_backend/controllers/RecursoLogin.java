package com.atissosa.cmdb_backend.controllers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.atissosa.cmdb_backend.logic.Sistema;

@Path("login")
public class RecursoLogin {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpHeaders headers) {
        String[] credencialesSeparadas = UtilidadesServicios.obtenerUsuarioContraseña(headers);
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().login(credencialesSeparadas[0], credencialesSeparadas[1]));
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void logout(@Context HttpHeaders headers) {
        String[] credencialesSeparadas = UtilidadesServicios.obtenerUsuarioContraseña(headers);
        Sistema.getInstancia().logout(credencialesSeparadas[0]);
    }
    
}
