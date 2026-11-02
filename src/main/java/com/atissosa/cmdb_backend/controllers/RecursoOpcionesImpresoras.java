package com.atissosa.cmdb_backend.controllers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.atissosa.cmdb_backend.logic.Sistema;

@Path("opcionesImpresoras")
public class RecursoOpcionesImpresoras {
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response devolverOpcionesImpresoras(){
        return UtilidadesServicios.nuevaResponse(Sistema.getInstancia().retornarOpcionesImpresoras());
    }
    
}
