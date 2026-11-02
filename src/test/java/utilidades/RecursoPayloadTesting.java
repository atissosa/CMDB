package utilidades;

import com.atissosa.cmdb_backend.controllers.UtilidadesServicios;
import com.atissosa.cmdb_backend.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;

/** Test-only HTTP fixture resource; never packaged in the executable JAR. */
@Path("cmdb16-fixtures")
@Produces(MediaType.APPLICATION_JSON)
public class RecursoPayloadTesting extends UtilidadesServicios {
    @GET @Path("output/{name}/{variant}")
    public Object output(@PathParam("name") String name, @PathParam("variant") String variant) throws Exception {
        return PayloadFixtureObjects.object(name, variant);
    }
    @GET @Path("envelope/{code}/{mode}")
    public Response envelope(@PathParam("code") int code, @PathParam("mode") String mode) {
        return nuevaResponse(Respuesta.nuevaRespuesta(code, mode.equals("null") ? null : new ArrayList<>()));
    }
    @POST @Path("input/DTODepartamentoCentral") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTODepartamentoCentral(DTODepartamentoCentral value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOEquipo") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOEquipo(DTOEquipo value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOFiltroEquipo") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOFiltroEquipo(DTOFiltroEquipo value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOImpresora") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOImpresora(DTOImpresora value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOItemHistoricoLugar") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOItemHistoricoLugar(DTOItemHistoricoLugar value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOLugar") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOLugar(DTOLugar value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOOtroEquipo") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOOtroEquipo(DTOOtroEquipo value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOPC") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOPC(DTOPC value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOParametroMantenimientoPrestamo") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOParametroMantenimientoPrestamo(DTOParametroMantenimientoPrestamo value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOPlantillaImpresora") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOPlantillaImpresora(DTOPlantillaImpresora value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOPlantillaOtroEquipo") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOPlantillaOtroEquipo(DTOPlantillaOtroEquipo value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOPlantillaPc") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOPlantillaPc(DTOPlantillaPc value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOPlantillaValidadora") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOPlantillaValidadora(DTOPlantillaValidadora value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOService") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOService(DTOService value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOSubAgencia") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOSubAgencia(DTOSubAgencia value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOUsuario") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOUsuario(DTOUsuario value) { return PayloadFixtureObjects.state(value); }
    @POST @Path("input/DTOValidadora") @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode inputDTOValidadora(DTOValidadora value) { return PayloadFixtureObjects.state(value); }
}
