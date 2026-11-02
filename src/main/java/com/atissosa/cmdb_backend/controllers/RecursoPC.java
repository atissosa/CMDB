package com.atissosa.cmdb_backend.controllers;

import com.atissosa.cmdb_backend.dto.DTOPC;
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

@Path("pc")
public class RecursoPC {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response altaPC(DTOPC nuevaPC) {
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().altaPc(nuevaPC));
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPc(@QueryParam("numeroSerie") String numeroSerie){
        Respuesta respuesta = Sistema.getInstancia().buscarEquipo(numeroSerie);
        Object retorno = respuesta.getRetorno();
        if(retorno != null && retorno instanceof DTOPC)
            respuesta.setRetorno(retorno);
        return UtilidadesServicios.nuevaResponse(respuesta);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarPC(@Context HttpHeaders headers, DTOPC pc) {
        String user = UtilidadesServicios.obtenerUsuarioContraseña(headers)[0];
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().modificarPC(pc, user));
    }
}
