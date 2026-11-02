package com.atissosa.cmdb_backend.controllers;

import com.atissosa.cmdb_backend.dto.DTOParametroMantenimientoPrestamo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.atissosa.cmdb_backend.logic.Sistema;

@Path("mantenimientoPrestamo")
public class RecursoMantenimientoPrestamos {
 
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listadoMantenimiento(@Context HttpHeaders headers){
        String[] credencialesSeparadas = UtilidadesServicios.obtenerUsuarioContraseña(headers);
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().listadoMantenimientoEquiposDePrestamo(credencialesSeparadas[0]));
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarParametrosMantenimientoPrestamo(DTOParametroMantenimientoPrestamo dto){
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().modificarParametrosMantenimientoPrestamos(dto));
    }
}
