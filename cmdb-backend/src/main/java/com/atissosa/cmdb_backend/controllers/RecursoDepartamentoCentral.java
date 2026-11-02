package com.atissosa.cmdb_backend.controllers;

import com.atissosa.cmdb_backend.dto.DTODepartamentoCentral;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.atissosa.cmdb_backend.logic.Sistema;

@Path("departamento")
public class RecursoDepartamentoCentral {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscar(@QueryParam("id") Integer id) {
        if(id == null)
            id = -1;
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().buscarDepartamentosCentral(id));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response alta(@Context HttpHeaders headers, DTODepartamentoCentral dtoDepartamento) {
        String user = UtilidadesServicios.obtenerUsuarioContraseña(headers)[0];
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().altaDepartamentoCentral(dtoDepartamento, user));
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modificar(@Context HttpHeaders headers, DTODepartamentoCentral dtoDepartamento) {
        String user = UtilidadesServicios.obtenerUsuarioContraseña(headers)[0];
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().modificarDepartamentoCentral(dtoDepartamento, user));
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response baja(@Context HttpHeaders headers, DTODepartamentoCentral dtoDepartamento) {
        String user = UtilidadesServicios.obtenerUsuarioContraseña(headers)[0];
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().bajaDepartamentoCentral(dtoDepartamento, user));
    }

}
