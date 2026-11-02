package com.atissosa.cmdb_backend.config;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

/** One credential contract, read through Spring's property sources, never business code getenv. */
public final class ConfiguracionSecretos {
    private ConfiguracionSecretos() { }

    public static void validar(ConfigurableEnvironment environment, String ambiente,
                               ConfiguracionBackend configuracion) {
        String usuario = resolver(environment, ambiente, "CMDB_DB_USERNAME");
        String password = resolver(environment, ambiente, "CMDB_DB_PASSWORD");
        rechazarAlternativas(environment, "cmdb.bd.usuario", "CMDB_DB_USERNAME");
        rechazarAlternativas(environment, "cmdb.bd.contrasenia", "CMDB_DB_PASSWORD");
        if (!usuario.equals(configuracion.bd().usuario())
                || !password.equals(configuracion.bd().contrasenia())) {
            throw new IllegalStateException("Credenciales: use únicamente CMDB_DB_USERNAME y CMDB_DB_PASSWORD");
        }
    }

    public static String resolver(ConfigurableEnvironment environment, String ambiente, String nombre) {
        PropertySource<?> source = environment.getPropertySources().get("systemEnvironment");
        // The only exception is the standard Spring test source fed by our disposable container.
        PropertySource<?> dynamic = environment.getPropertySources().get("Dynamic Test Properties");
        Object value = "test".equals(ambiente) && dynamic != null ? dynamic.getProperty(nombre) : null;
        if (value == null && source != null) value = source.getProperty(nombre);
        if (!(value instanceof String text) || text.isBlank()) {
            throw new IllegalStateException("Variable obligatoria ausente o vacía: " + nombre);
        }
        if (text.equalsIgnoreCase("changeme") || text.startsWith("${")
                || (text.startsWith("<") && text.endsWith(">"))) {
            throw new IllegalStateException("La variable " + nombre + " contiene un placeholder; inyecte un valor externo válido");
        }
        return text; // No trimming or transformation of valid passwords.
    }

    private static void rechazarAlternativas(ConfigurableEnvironment environment, String property, String variable) {
        for (var source : environment.getPropertySources()) {
            if (source.getName().equals("configurationProperties")) continue; // Aggregate of the underlying sources.
            Object value = source.getProperty(property);
            if (value != null && !value.equals("${" + variable + ":}")) {
                throw new IllegalStateException("Entrada alternativa de credenciales no admitida: "
                        + property + "; use " + variable);
            }
            if (!source.getName().equals("systemEnvironment")
                    && !source.getName().equals("Dynamic Test Properties")
                    && source.getProperty(variable) != null) {
                throw new IllegalStateException("La variable " + variable + " debe inyectarse mediante el entorno");
            }
        }
    }
}
