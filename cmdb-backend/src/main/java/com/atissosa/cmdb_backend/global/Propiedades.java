package com.atissosa.cmdb_backend.global;

import com.atissosa.cmdb_backend.config.ConfiguracionBackend;
import com.atissosa.cmdb_backend.persistency.BD;
import com.atissosa.cmdb_backend.persistency.ConfiguradorDataSource;

/** Transitional facade for legacy consumers. Spring resolves and validates all values first. */
public abstract class Propiedades {
    public enum ProveedoresBD { MSSQLSERVER }
    public enum TiposLogin { CLASICO, ACTIVE_DIRECTORY }

    public static ProveedoresBD BD_PROVEEDOR;
    public static String BD_SERVIDOR;
    public static String BD_PUERTO;
    public static String BD_BD;
    public static String BD_USUARIO;
    public static String BD_CONTRASENIA;
    public static TiposLogin LOGIN_TIPO;
    public static String DEPARTAMENTO_DEFECTO;

    public static void configurar(ConfiguracionBackend configuracion) {
        var bd = configuracion.bd();
        BD_PROVEEDOR = bd.proveedor();
        BD_SERVIDOR = bd.servidor();
        // Keep the legacy JDBC assembler, with explicitly typed TLS settings.
        BD_PUERTO = bd.puerto() + ";encrypt=" + bd.encrypt()
                + ";trustServerCertificate=" + bd.trustServerCertificate();
        BD_BD = bd.nombre();
        BD_USUARIO = bd.usuario();
        BD_CONTRASENIA = bd.contrasenia();
        LOGIN_TIPO = configuracion.login().tipo();
        DEPARTAMENTO_DEFECTO = configuracion.departamento().defecto();
        if (!BD.tieneDataSourceConfigurado()) {
            BD.configurarDataSource(ConfiguradorDataSource.crearDesde(configuracion, "cmdbLegacyPool"));
        }
    }
}
