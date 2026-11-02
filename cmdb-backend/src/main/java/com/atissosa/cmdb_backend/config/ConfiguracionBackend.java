package com.atissosa.cmdb_backend.config;

import com.atissosa.cmdb_backend.global.Propiedades.ProveedoresBD;
import com.atissosa.cmdb_backend.global.Propiedades.TiposLogin;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

/** Immutable values bound by Spring Boot, without exposing credentials in toString. */
public record ConfiguracionBackend(Bd bd, Login login, Departamento departamento, Cors cors) {
    public record Pool(Integer minIdle, Integer maxSize, Long connectionTimeoutMs,
                       Long idleTimeoutMs, Long maxLifetimeMs, Long validationTimeoutMs) { }

    public record Bd(ProveedoresBD proveedor, String servidor, Integer puerto, String nombre,
                     String usuario, String contrasenia, Boolean encrypt,
                     Boolean trustServerCertificate, Pool pool) {
        @Override
        public String toString() {
            return "Bd[credenciales y valores omitidos]";
        }
    }

    public record Login(TiposLogin tipo) { }
    public record Departamento(String defecto) { }
    public record Cors(List<String> allowedOrigins, List<String> allowedMethods,
                       List<String> allowedHeaders, Boolean allowCredentials, Integer maxAgeSeconds) { }

    public void validar(String ambiente) {
        requerido(bd, "cmdb.bd");
        requerido(bd.proveedor(), "cmdb.bd.proveedor (MSSQLSERVER)");
        texto(bd.servidor(), "cmdb.bd.servidor");
        requerido(bd.puerto(), "cmdb.bd.puerto");
        if (bd.puerto() < 1 || bd.puerto() > 65535) {
            throw new IllegalStateException("Configuración inválida: cmdb.bd.puerto debe estar entre 1 y 65535");
        }
        texto(bd.nombre(), "cmdb.bd.nombre");
        texto(bd.usuario(), "cmdb.bd.usuario");
        texto(bd.contrasenia(), "cmdb.bd.contrasenia");
        requerido(bd.encrypt(), "cmdb.bd.encrypt");
        requerido(bd.trustServerCertificate(), "cmdb.bd.trust-server-certificate");
        requerido(bd.pool(), "cmdb.bd.pool");
        requerido(bd.pool().minIdle(), "cmdb.bd.pool.min-idle");
        requerido(bd.pool().maxSize(), "cmdb.bd.pool.max-size");
        requerido(bd.pool().connectionTimeoutMs(), "cmdb.bd.pool.connection-timeout-ms");
        requerido(bd.pool().idleTimeoutMs(), "cmdb.bd.pool.idle-timeout-ms");
        requerido(bd.pool().maxLifetimeMs(), "cmdb.bd.pool.max-lifetime-ms");
        requerido(bd.pool().validationTimeoutMs(), "cmdb.bd.pool.validation-timeout-ms");
        if (bd.pool().minIdle() < 0 || bd.pool().maxSize() < 1 || bd.pool().minIdle() > bd.pool().maxSize()) {
            throw new IllegalStateException("Configuración inválida: cmdb.bd.pool.min-idle/max-size fuera de rango");
        }
        if (bd.pool().connectionTimeoutMs() < 250L || bd.pool().idleTimeoutMs() < 0L
                || bd.pool().maxLifetimeMs() < 0L || bd.pool().validationTimeoutMs() < 250L) {
            throw new IllegalStateException("Configuración inválida: timeout de pool fuera de rango mínimo");
        }
        requerido(login, "cmdb.login");
        requerido(login.tipo(), "cmdb.login.tipo (CLASICO, ACTIVE_DIRECTORY)");
        requerido(departamento, "cmdb.departamento");
        texto(departamento.defecto(), "cmdb.departamento.defecto");

        requerido(cors, "cmdb.cors");
        requerido(cors.allowedMethods(), "cmdb.cors.allowed-methods");
        requerido(cors.allowedHeaders(), "cmdb.cors.allowed-headers");
        requerido(cors.allowCredentials(), "cmdb.cors.allow-credentials");
        requerido(cors.maxAgeSeconds(), "cmdb.cors.max-age-seconds");
        if (cors.maxAgeSeconds() < 0) {
            throw new IllegalStateException("Configuración inválida: cmdb.cors.max-age-seconds debe ser >= 0");
        }
        if (cors.allowedMethods().isEmpty()) {
            throw new IllegalStateException("Configuración obligatoria vacía: cmdb.cors.allowed-methods");
        }
        if (cors.allowedHeaders().isEmpty()) {
            throw new IllegalStateException("Configuración obligatoria vacía: cmdb.cors.allowed-headers");
        }
        validarMetodosCors(cors.allowedMethods());
        validarHeadersCors(cors.allowedHeaders());

        List<String> allowedOrigins = cors.allowedOrigins() == null ? List.of() : cors.allowedOrigins();
        allowedOrigins.forEach(ConfiguracionBackend::validarOrigenCors);
        if (cors.allowCredentials() && allowedOrigins.isEmpty()) {
            throw new IllegalStateException("Configuración inválida: cmdb.cors.allow-credentials requiere cmdb.cors.allowed-origins explícitos");
        }
        if ("prod".equals(ambiente) && allowedOrigins.isEmpty()) {
            throw new IllegalStateException("Configuración inválida: en prod defina cmdb.cors.allowed-origins explícitos");
        }
    }

    private static void validarMetodosCors(List<String> methods) {
        for (String method : methods) {
            texto(method, "cmdb.cors.allowed-methods");
            String normalized = method.trim().toUpperCase(Locale.ROOT);
            if (!normalized.matches("[A-Z]+")) {
                throw new IllegalStateException("Configuración inválida: cmdb.cors.allowed-methods contiene un valor no válido");
            }
        }
    }

    private static void validarHeadersCors(List<String> headers) {
        for (String header : headers) {
            texto(header, "cmdb.cors.allowed-headers");
            String normalized = header.trim().toLowerCase(Locale.ROOT);
            if (!normalized.matches("[a-z0-9!#$%&'*+.^_`|~-]+")) {
                throw new IllegalStateException("Configuración inválida: cmdb.cors.allowed-headers contiene un valor no válido");
            }
        }
    }

    private static void validarOrigenCors(String origin) {
        texto(origin, "cmdb.cors.allowed-origins");
        if ("null".equalsIgnoreCase(origin.trim())) {
            return;
        }
        if ("*".equals(origin.trim())) {
            throw new IllegalStateException("Configuración inválida: cmdb.cors.allowed-origins no admite comodines");
        }
        URI uri;
        try {
            uri = new URI(origin);
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Configuración inválida: cmdb.cors.allowed-origins contiene un URI inválido");
        }
        String scheme = uri.getScheme();
        if (!("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
            throw new IllegalStateException("Configuración inválida: cmdb.cors.allowed-origins debe usar http o https");
        }
        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new IllegalStateException("Configuración inválida: cmdb.cors.allowed-origins requiere host explícito");
        }
        if (uri.getPort() < 0) {
            throw new IllegalStateException("Configuración inválida: cmdb.cors.allowed-origins requiere puerto explícito");
        }
        if (uri.getPath() != null && !uri.getPath().isBlank()) {
            throw new IllegalStateException("Configuración inválida: cmdb.cors.allowed-origins no debe incluir path");
        }
        if (uri.getQuery() != null || uri.getFragment() != null || uri.getUserInfo() != null) {
            throw new IllegalStateException("Configuración inválida: cmdb.cors.allowed-origins solo admite scheme, host y puerto");
        }
    }

    private static void requerido(Object valor, String clave) {
        if (valor == null) {
            throw new IllegalStateException("Configuración obligatoria ausente: " + clave);
        }
    }

    private static void texto(String valor, String clave) {
        requerido(valor, clave);
        if (valor.isBlank()) {
            throw new IllegalStateException("Configuración obligatoria vacía: " + clave);
        }
    }
}
