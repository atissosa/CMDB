package com.atissosa.cmdb_backend.config;

import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

/** Keep the original JSON-B wire conventions while retaining the chosen Jersey/Jackson provider. */
@Provider
public final class CompatibilidadJson implements ContextResolver<ObjectMapper> {
    private final ObjectMapper mapper;

    public CompatibilidadJson() {
        mapper = new ObjectMapper().findAndRegisterModules();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(MapperFeature.USE_STD_BEAN_NAMING, true);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        mapper.addMixIn(DTOFiltroEquipo.class, FiltroNulos.class);
    }

    /** JSON-B models this property as primitive through getEsPrestamo(), despite its boxed setter. */
    private abstract static class FiltroNulos {
        @JsonSetter(nulls = Nulls.FAIL)
        abstract void setEsPrestamo(Boolean value);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) { return mapper; }
}
