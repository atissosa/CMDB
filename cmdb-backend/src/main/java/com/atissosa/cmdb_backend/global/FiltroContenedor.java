package com.atissosa.cmdb_backend.global;

import com.atissosa.cmdb_backend.config.ConfiguracionBackend;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

public class FiltroContenedor implements ContainerResponseFilter {
    private static final String HEADER_ORIGIN = "Origin";
    private static final String HEADER_VARY = "Vary";
    private static final String HEADER_REQUEST_METHOD = "Access-Control-Request-Method";
    private static final String HEADER_REQUEST_HEADERS = "Access-Control-Request-Headers";

    private final Set<String> allowedOrigins;
    private final String allowMethods;
    private final String allowHeaders;
    private final boolean allowCredentials;
    private final int maxAgeSeconds;

    public FiltroContenedor(ConfiguracionBackend.Cors cors) {
        this.allowedOrigins = new LinkedHashSet<>();
        if (cors.allowedOrigins() != null) {
            cors.allowedOrigins().stream()
                    .filter(value -> value != null)
                    .map(value -> value.trim())
                    .filter(value -> !value.isEmpty())
                    .forEach(this.allowedOrigins::add);
        }
        this.allowMethods = joinNormalized(cors.allowedMethods(), true);
        this.allowHeaders = joinNormalized(cors.allowedHeaders(), false);
        this.allowCredentials = cors.allowCredentials();
        this.maxAgeSeconds = cors.maxAgeSeconds();
    }

    /*  Para permitir recibir peticiones entre dominios
        Desde el backend en un servidor separado    */
    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext responseContext) throws IOException {
        String origin = requestContext.getHeaderString(HEADER_ORIGIN);
        if (origin == null || origin.isBlank()) {
            return;
        }

        addVary(responseContext, HEADER_ORIGIN);
        boolean preflight = "OPTIONS".equalsIgnoreCase(requestContext.getMethod())
                && requestContext.getHeaderString(HEADER_REQUEST_METHOD) != null;
        if (preflight) {
            addVary(responseContext, HEADER_REQUEST_METHOD);
            addVary(responseContext, HEADER_REQUEST_HEADERS);
        }
        if (!allowedOrigins.contains(origin)) {
            return;
        }

        responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", origin);
        responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", allowMethods);
        responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", allowHeaders);
        responseContext.getHeaders().putSingle("Access-Control-Max-Age", Integer.toString(maxAgeSeconds));
        if (allowCredentials) {
            responseContext.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
        } else {
            responseContext.getHeaders().remove("Access-Control-Allow-Credentials");
        }
    }

    private static String joinNormalized(List<String> values, boolean upperCase) {
        return values.stream()
                .filter(value -> value != null)
                .map(value -> value.trim())
                .filter(s -> !s.isEmpty())
                .map(s -> upperCase ? s.toUpperCase(Locale.ROOT) : s.toLowerCase(Locale.ROOT))
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
    }

    private static void addVary(ContainerResponseContext responseContext, String value) {
        Set<String> vary = new LinkedHashSet<>();
        for (Object headerValue : responseContext.getHeaders().getOrDefault(HEADER_VARY, List.of())) {
            if (headerValue == null) {
                continue;
            }
            Arrays.stream(headerValue.toString().split(","))
                    .map(part -> part.trim())
                    .filter(part -> !part.isEmpty())
                    .forEach(vary::add);
        }
        vary.add(value);
        responseContext.getHeaders().putSingle(HEADER_VARY, String.join(", ", vary));
    }

}
