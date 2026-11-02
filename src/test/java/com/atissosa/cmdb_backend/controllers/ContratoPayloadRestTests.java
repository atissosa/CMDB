package com.atissosa.cmdb_backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import utilidades.PayloadHttpTesting;
import utilidades.RecursoPayloadTesting;
import utilidades.SqlServerTesting;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(ContratoPayloadRestTests.Fixtures.class)
class ContratoPayloadRestTests {
    @Autowired Environment environment;
    @DynamicPropertySource
    static void aislado(DynamicPropertyRegistry registry) {
        SqlServerTesting.registrarPropiedades(registry);
        registry.add("server.address",()->"127.0.0.1");
    }
    @TestConfiguration(proxyBeanMethods=false)
    static class Fixtures {
        @Bean static BeanPostProcessor resource() {
            return new BeanPostProcessor() {
                @Override public Object postProcessBeforeInitialization(Object bean,String name) {
                    if(bean instanceof ResourceConfig config)config.register(RecursoPayloadTesting.class);
                    return bean;
                }
            };
        }
    }
    private PayloadHttpTesting http() throws Exception {
        return new PayloadHttpTesting(URI.create("http://127.0.0.1:"+environment.getRequiredProperty("local.server.port")
                +environment.getProperty("server.servlet.context-path","")));
    }
    private void report(String name,Object result) throws Exception {
        var folder=Path.of("target/payload-http-reports");Files.createDirectories(folder);
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(folder.resolve(name+".json").toFile(),result);
    }
    @Test void conservaLosDtoSegunJsonbOriginal() throws Exception {report("dto-outputs",http().outputs());}
    @Test void conservaEntradasOmisionesNulosYCoerciones() throws Exception {report("dto-inputs",http().inputs());}
    @Test void preservaLos30CodigosYEstadosHttp() throws Exception {report("envelopes",http().envelopes());}
    @Test void verificaPayloadsEnEndpointsReales() throws Exception {report("production-endpoints",http().realEndpoints());}
    @Test void respetaMayusculasYNumerosDelFrontend() throws Exception {report("maintenance",http().maintenance());}
    @Test void rechazaJsonSinExponerValoresOInternos() throws Exception {report("invalid-json",http().invalidEntities());}
}
