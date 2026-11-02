package com.atissosa.cmdb_backend.controllers;

import com.atissosa.cmdb_backend.dto.DTOValidadora;
import com.atissosa.cmdb_backend.dto.Respuesta;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.atissosa.cmdb_backend.logic.Sistema;

@Path("validadora")
public class RecursoValidadora {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response altaValidadora(DTOValidadora nuevaValidadora) {
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().altaValidadora(nuevaValidadora));
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarValidadora(@QueryParam("numeroSerie") String numeroSerie){
        Respuesta respuesta = Sistema.getInstancia().buscarEquipo(numeroSerie);
        Object retorno = respuesta.getRetorno();
        if(retorno != null && retorno instanceof DTOValidadora)
            respuesta.setRetorno(retorno);
        return UtilidadesServicios.nuevaResponse(respuesta);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarValidadora(@Context HttpHeaders headers, DTOValidadora validadora) {
        String user = UtilidadesServicios.obtenerUsuarioContraseña(headers)[0];
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().modificarValidadora(validadora, user));
    }
}