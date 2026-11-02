package com.atissosa.cmdb_backend.controllers;

import com.atissosa.cmdb_backend.dto.DTOPlantillaImpresora;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.atissosa.cmdb_backend.logic.Sistema;

@Path("plantillaIMPRESORA")
public class RecursoPlantillaImpresora {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlantillasImpresora(@QueryParam("marca") String marca){
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().getPlantillasImpresoras(marca));
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlantillaImpresora(DTOPlantillaImpresora dto){
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().buscarPlantillaImpresora(dto));
    }
}
