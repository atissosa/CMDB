package com.atissosa.cmdb_backend.global;

import com.atissosa.cmdb_backend.config.ConfiguracionAplicacion;
import com.atissosa.cmdb_backend.config.ConfiguracionBackend;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.persistency.BD;
import javax.sql.DataSource;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/** Initialize the legacy facade before Jersey makes resources available. */
@Component
public class ContextoAplicacion {
    public ContextoAplicacion(ConfiguracionBackend configuracion, Environment environment, DataSource dataSource) {
        BD.configurarDataSource(dataSource);
        Propiedades.configurar(configuracion);
        Respuesta.cargarMensajes();
        var bd = configuracion.bd();
        var pool = bd.pool();
        LoggerFactory.getLogger(ContextoAplicacion.class).info(
                "CMDB configuración aplicada: ambiente={} proveedor={} servidor={} puerto={} "
                + "base={} login={} departamento={} encrypt={} trustServerCertificate={} "
                + "pool[minIdle={},maxSize={},connectionTimeoutMs={},idleTimeoutMs={},maxLifetimeMs={}] legacyReady=true",
                ConfiguracionAplicacion.ambiente(environment), Propiedades.BD_PROVEEDOR,
                Propiedades.BD_SERVIDOR, bd.puerto(), Propiedades.BD_BD, Propiedades.LOGIN_TIPO,
                Propiedades.DEPARTAMENTO_DEFECTO, bd.encrypt(), bd.trustServerCertificate(),
                pool.minIdle(), pool.maxSize(), pool.connectionTimeoutMs(), pool.idleTimeoutMs(), pool.maxLifetimeMs());
    }
}
