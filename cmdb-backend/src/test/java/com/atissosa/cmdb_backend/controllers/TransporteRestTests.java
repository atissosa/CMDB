package com.atissosa.cmdb_backend.controllers;

import com.atissosa.cmdb_backend.global.FiltroContenedor;
import jakarta.ws.rs.Path;
import java.net.URI;
import java.util.HashSet;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import utilidades.SqlServerTesting;
import utilidades.TransporteHttpTesting;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TransporteRestTests {
    @Autowired Environment environment;
    @Autowired ResourceConfig jersey;

    @DynamicPropertySource
    static void configuracionAislada(DynamicPropertyRegistry registry) {
        SqlServerTesting.registrarPropiedades(registry);
        registry.add("server.address", () -> "127.0.0.1");
    }

    private TransporteHttpTesting http() {
        return new TransporteHttpTesting(URI.create("http://127.0.0.1:" + environment.getRequiredProperty("local.server.port") + environment.getProperty("server.servlet.context-path", "")));
    }

    @Test
    void registraLos24RecursosUnaSolaVez() throws Exception {
        var actual = new HashSet<String>();
        jersey.getClasses().stream().filter(c -> c.isAnnotationPresent(Path.class)).forEach(c -> actual.add(c.getSimpleName()));
        var expected = new HashSet<String>();
        for (var resource : TransporteHttpTesting.routes()) expected.add(resource.path("class").asText());
        assertEquals(expected, actual);
        assertTrue(jersey.getInstances().stream().anyMatch(FiltroContenedor.class::isInstance));
        assertEquals(24, http().registro().size());
    }

    @Test
    void vinculaQueryParamsYSerializaLecturas() throws Exception { http().consulta(); }

    @Test
    void deserializaDTOYDelegaAltasCambiosYBajas() throws Exception { http().escritura(); }

    @Test
    void vinculaCabecerasYConservaRespuestasDeLogin() throws Exception { http().cabeceras(); }

    @Test
    void rechazaJsonInvalidoYTiposNoSoportados() throws Exception { http().entidadesInvalidas(); }

    @Test
    void conservaUnSoloFlujoCors() throws Exception { http().cors(); }
}
