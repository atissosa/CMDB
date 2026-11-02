package com.atissosa.cmdb_backend.global;

import com.atissosa.cmdb_backend.config.ConfiguracionBackend;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/** Explicit registration also works from the Spring Boot executable jar. */
@Component
@ApplicationPath("recursos")
public class Aplicacion extends ResourceConfig {

    public Aplicacion(ContextoAplicacion contexto, ConfiguracionBackend configuracion) {
        register(org.glassfish.jersey.jackson.JacksonFeature.withoutExceptionMappers());
        register(com.atissosa.cmdb_backend.config.CompatibilidadJson.class);
        register(com.atissosa.cmdb_backend.config.ErroresJson.Procesamiento.class);
        register(com.atissosa.cmdb_backend.config.ErroresJson.Solicitud.class);
        register(new com.atissosa.cmdb_backend.global.FiltroContenedor(configuracion.cors()));
        register(com.atissosa.cmdb_backend.controllers.RecursoComboLugares.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoConexionesImpresoras.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoDepartamentoCentral.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoEquipo.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoEquipos.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoEstadosEquipos.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoHistoricoEquipo.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoImpresora.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoLogin.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoLugar.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoMantenimientoPrestamos.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoOpcionesImpresoras.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoOtroEquipo.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoPC.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoPlantillaImpresora.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoPlantillaOtroEquipo.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoPlantillaPc.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoPlantillaValidadora.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoPrestamo.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoReparacion.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoService.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoSubAgencia.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoTiposEquipos.class);
        register(com.atissosa.cmdb_backend.controllers.RecursoValidadora.class);
    }
}
