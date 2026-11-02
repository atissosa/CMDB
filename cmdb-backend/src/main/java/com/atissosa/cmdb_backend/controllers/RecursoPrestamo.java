package com.atissosa.cmdb_backend.controllers;

import com.atissosa.cmdb_backend.dto.DTOEquipo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.atissosa.cmdb_backend.logic.Sistema;

@Path("prestamo")
public class RecursoPrestamo {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getParametrosMantenimiento(){
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().getParametrosMantenimiento());
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response prestarEquipoSubagencia(@Context HttpHeaders headers, DTOEquipo equipo) {
        String user = UtilidadesServicios.obtenerUsuarioContraseña(headers)[0];
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().prestarEquipoSubagencia(equipo, user));
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response devolverEquipoCentral(@Context HttpHeaders headers, DTOEquipo equipo) {
        String user = UtilidadesServicios.obtenerUsuarioContraseña(headers)[0];
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().devolverEquipoCentral(equipo, user));
    }

}
