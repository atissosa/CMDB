package com.atissosa.cmdb_backend.controllers;

import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.atissosa.cmdb_backend.logic.Sistema;

@Path("equipos")
public class RecursoEquipos {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEquipos(@Context HttpHeaders headers, DTOFiltroEquipo dtoFiltroEquipo) {
        String user = UtilidadesServicios.obtenerUsuarioContraseña(headers)[0];
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().buscarEquipos(dtoFiltroEquipo));
    }
    
}
