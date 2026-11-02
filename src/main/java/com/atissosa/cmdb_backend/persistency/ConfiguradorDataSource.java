package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.config.ConfiguracionBackend;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/** Build a pooled SQL Server DataSource from typed CMDB configuration. */
public final class ConfiguradorDataSource {
    private ConfiguradorDataSource() { }

    public static HikariDataSource crearDesde(ConfiguracionBackend configuracion, String poolName) {
        var bd = configuracion.bd();
        var pool = bd.pool();
        HikariConfig hikari = new HikariConfig();
        hikari.setPoolName(poolName);
        hikari.setJdbcUrl("jdbc:sqlserver://" + bd.servidor() + ":" + bd.puerto()
                + ";databaseName=" + bd.nombre() + ";encrypt=" + bd.encrypt()
                + ";trustServerCertificate=" + bd.trustServerCertificate());
        hikari.setUsername(bd.usuario());
        hikari.setPassword(bd.contrasenia());
        hikari.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        hikari.setMinimumIdle(pool.minIdle());
        hikari.setMaximumPoolSize(pool.maxSize());
        hikari.setConnectionTimeout(pool.connectionTimeoutMs());
        hikari.setIdleTimeout(pool.idleTimeoutMs());
        hikari.setMaxLifetime(pool.maxLifetimeMs());
        hikari.setValidationTimeout(pool.validationTimeoutMs());
        return new HikariDataSource(hikari);
    }
}
