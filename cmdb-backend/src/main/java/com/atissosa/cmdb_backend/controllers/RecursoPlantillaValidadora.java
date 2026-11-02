package com.atissosa.cmdb_backend.controllers;

import com.atissosa.cmdb_backend.dto.DTOPlantillaValidadora;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.atissosa.cmdb_backend.logic.Sistema;

@Path("plantillaVALIDADORA")
public class RecursoPlantillaValidadora {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlantillasValidadoras(@QueryParam("marca") String marca){
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().getPlantillasValidadoras(marca));
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlantillaPc(DTOPlantillaValidadora dto){
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().buscarPlantillaValidadora(dto));
    }
}
