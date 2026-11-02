package com.atissosa.cmdb_backend.controllers;

import com.atissosa.cmdb_backend.dto.DTOEquipo;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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

@Path("equipo")
public class RecursoEquipo {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEquipos(@QueryParam("numeroSerie") String numeroSerie) {
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().buscarEquipos(numeroSerie));
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response darEquipoDeBaja(DTOFiltroEquipo dtoFiltroEquipo){
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().darEquipoDeBaja(dtoFiltroEquipo));
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response asignarParaPrestamo(DTOFiltroEquipo dtoFiltroEquipo){
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().asignarEquipoParaPrestamo(dtoFiltroEquipo));
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response despacharEquipo(@Context HttpHeaders headers, DTOEquipo equipo){
        String user = UtilidadesServicios.obtenerUsuarioContraseña(headers)[0];
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().despacharEquipo(equipo, user));
    }
}
