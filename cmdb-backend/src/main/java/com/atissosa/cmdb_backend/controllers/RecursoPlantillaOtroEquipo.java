package com.atissosa.cmdb_backend.controllers;

import com.atissosa.cmdb_backend.dto.DTOPlantillaOtroEquipo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.atissosa.cmdb_backend.logic.Sistema;

@Path("plantillaOTROEQUIPO")
public class RecursoPlantillaOtroEquipo {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlantillasOtroEquipo(@QueryParam("marca") String marca, @QueryParam("tipo") String tipo){
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().getPlantillasOtroEquipo(tipo, marca));
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlantillaOtroEquipo(DTOPlantillaOtroEquipo dto){
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().buscarPlantillaOtroEquipo(dto));
    }
}
