package com.atissosa.cmdb_backend.controllers;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import utilidades.ContratoRutasHttpTesting;
import utilidades.SqlServerTesting;
import utilidades.TransporteHttpTesting;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ContratoRutasRestTests {
    @Autowired Environment environment;

    @DynamicPropertySource
    static void configuracionAislada(DynamicPropertyRegistry registry) {
        SqlServerTesting.registrarPropiedades(registry);
        registry.add("server.address", () -> "127.0.0.1");
    }

    private URI origin() { return URI.create("http://127.0.0.1:" + environment.getRequiredProperty("local.server.port")); }
    private ContratoRutasHttpTesting contract() throws Exception {
        assertEquals(ContratoRutasHttpTesting.matrix().path("servlet_context").asText(), environment.getProperty("server.servlet.context-path"));
        return new ContratoRutasHttpTesting(origin());
    }
    private void report(String name, Object results) throws Exception {
        Path folder = Path.of("target/contract-http-reports");
        Files.createDirectories(folder);
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(folder.resolve(name + ".json").toFile(), results);
    }

    @Test
    void mantieneLas51OperacionesEnLaUrlCompleta() throws Exception { report("operations", contract().todasLasOperaciones()); }

    @Test
    void conservaLasLlamadasInventariadasDelFrontend() throws Exception { report("frontend", contract().llamadasFrontend()); }

    @Test
    void contrastaParametrosAusentesVaciosYCodificados() throws Exception { report("query-parameters", contract().parametros()); }

    @Test
    void rechazaRutasYVerbosNoSoportados() throws Exception { report("invalid-routes", contract().rutasInvalidas()); }

    @Test
    void conservaDeleteConCuerpoYCabeceraDelFrontend() throws Exception {
        contract();
        var http = new TransporteHttpTesting(URI.create(origin() + environment.getProperty("server.servlet.context-path")));
        report("frontend-delete-body", http.escrituraConCabeceraFrontend());
    }
}
