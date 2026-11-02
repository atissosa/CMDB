package com.atissosa.cmdb_backend.config;

import java.util.Arrays;
import java.util.Set;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration(proxyBeanMethods = false)
public class ConfiguracionAplicacion {
    public static String ambiente(Environment environment) {
        var activos = Arrays.stream(environment.getActiveProfiles())
                .filter(Set.of("dev", "test", "prod")::contains).distinct().toList();
        if (activos.size() != 1) {
            throw new IllegalStateException(
                    "Seleccione exactamente un perfil de ambiente explícito: dev, test o prod "
                    + "(SPRING_PROFILES_ACTIVE o --spring.profiles.active)");
        }
        return activos.getFirst();
    }

    @Bean
    public ConfiguracionBackend configuracionBackend(ConfigurableEnvironment environment) {
        String ambiente = ambiente(environment);
        // Validate canonical variables before binding could report an internal/legacy property name.
        ConfiguracionSecretos.resolver(environment, ambiente, "CMDB_DB_USERNAME");
        ConfiguracionSecretos.resolver(environment, ambiente, "CMDB_DB_PASSWORD");
        ConfiguracionBackend configuracion;
        try {
            configuracion = Binder.get(environment)
                    .bind("cmdb", Bindable.of(ConfiguracionBackend.class))
                    .orElseThrow(() -> new IllegalStateException("Falta la configuración cmdb"));
        } catch (BindException ex) {
            // Do not propagate rejected values or a cause that could contain credentials.
            throw new IllegalStateException("Configuración inválida en " + ex.getName()
                    + ": revise el tipo o valor permitido (proveedor MSSQLSERVER; "
                    + "login CLASICO o ACTIVE_DIRECTORY; puerto entero; TLS booleano; CORS tipado)");
        }
        ConfiguracionSecretos.validar(environment, ambiente, configuracion);
        configuracion.validar(ambiente);
        return configuracion;
    }
}
