package com.atissosa.cmdb_backend.controllers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.atissosa.cmdb_backend.logic.Sistema;

@Path("historicoEquipo")
public class RecursoHistoricoEquipo {
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistoricoEquipos(@QueryParam("numeroSerie") String numeroSerie){
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().buscarHistoricoEquipos(numeroSerie));
    }
    
}
