package com.atissosa.cmdb_backend.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Transport errors stay HTTP 400 without disclosing parser internals or supplied values. */
public final class ErroresJson {
    private ErroresJson() { }

    private static Response invalido() {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN_TYPE.withCharset("UTF-8"))
                .entity("JSON inválido.").build();
    }

    @Provider
    public static final class Procesamiento implements ExceptionMapper<JsonProcessingException> {
        @Override public Response toResponse(JsonProcessingException exception) { return invalido(); }
    }

    @Provider
    public static final class Solicitud implements ExceptionMapper<BadRequestException> {
        @Override public Response toResponse(BadRequestException exception) { return invalido(); }
    }
}
