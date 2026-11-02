package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import utilidades.SqlServerTesting;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class PoolPersistenciaIntegrationTests {

    @Autowired
    DataSource dataSource;

    @DynamicPropertySource
    static void configuracionAislada(DynamicPropertyRegistry registry) {
        SqlServerTesting.registrarPropiedades(registry);
        registry.add("cmdb.bd.pool.min-idle", () -> 1);
        registry.add("cmdb.bd.pool.max-size", () -> 2);
        registry.add("cmdb.bd.pool.connection-timeout-ms", () -> 3000L);
        registry.add("cmdb.bd.pool.idle-timeout-ms", () -> 10000L);
        registry.add("cmdb.bd.pool.max-lifetime-ms", () -> 120000L);
        registry.add("cmdb.bd.pool.validation-timeout-ms", () -> 1500L);
    }

    @Test
    void usaConexionesDesdePoolGestionado() throws Exception {
        assertInstanceOf(HikariDataSource.class, dataSource);
        assertTrue(BD.tieneDataSourceConfigurado());
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.getClass().getName().toLowerCase().contains("hikari"));
        }
        assertEquals(0, ((HikariDataSource) dataSource).getHikariPoolMXBean().getActiveConnections());
    }

    @Test
    void confirmaYRevierteTransaccionesManteniendoSemantica() throws Exception {
        String usuarioRollback = "txrb_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String usuarioCommit = "txok_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        BD bdRollback = BD.nuevaConexion();
        try {
            bdRollback.conectar(true);
            bdRollback.ejecutarSentencia(insertUsuario(), parametrosUsuario(usuarioRollback));
            bdRollback.rollback();
        } finally {
            bdRollback.desconectar();
        }
        assertEquals(0, contarUsuarios(usuarioRollback));

        BD bdCommit = BD.nuevaConexion();
        try {
            bdCommit.conectar(true);
            bdCommit.ejecutarSentencia(insertUsuario(), parametrosUsuario(usuarioCommit));
            bdCommit.commit();
        } finally {
            bdCommit.desconectar();
        }
        assertEquals(1, contarUsuarios(usuarioCommit));

        BD cleanup = BD.nuevaConexion();
        try {
            cleanup.conectar();
            cleanup.ejecutarSentencia("DELETE FROM dbo.Usuarios WHERE Usuario = ?", parametrosUno(usuarioCommit));
        } finally {
            cleanup.desconectar();
        }
    }

    @Test
    void liberaConexionesYStatementsAnteExcepcion() throws Exception {
        HikariDataSource hikari = (HikariDataSource) dataSource;
        BD bd = BD.nuevaConexion();
        try {
            bd.conectar();
            ExcepcionConexion error = assertThrows(ExcepcionConexion.class,
                    () -> bd.ejecutarSentencia("UPDATE TablaInexistente SET x = 1", null));
            assertNotNull(error.getMessage());
        } finally {
            bd.desconectar();
        }
        assertEquals(0, hikari.getHikariPoolMXBean().getActiveConnections());
    }

    @Test
    void reutilizaPoolPequenioSinAgotamiento() throws Exception {
        HikariDataSource hikari = (HikariDataSource) dataSource;
        for (int i = 0; i < 20; i++) {
            BD bd = BD.nuevaConexion();
            try {
                bd.conectar();
                try (var rs = bd.ejecutarConsulta("SELECT TOP 1 Id FROM dbo.Usuarios", null)) {
                    if (rs.next()) {
                        assertTrue(rs.getInt(1) > 0);
                    }
                }
            } finally {
                bd.desconectar();
            }
        }
        assertTrue(hikari.getHikariPoolMXBean().getTotalConnections() <= 2);
        assertEquals(0, hikari.getHikariPoolMXBean().getActiveConnections());
    }

    private static String insertUsuario() {
        return "INSERT dbo.Usuarios (Usuario, Contrasena, IdRol, IdDepartamento, Activo) "
                + "SELECT ?, ?, IdRol, IdDepartamento, Activo FROM dbo.Usuarios WHERE Id = ?";
    }

    private static ArrayList<Object> parametrosUsuario(String usuario) {
        ArrayList<Object> parametros = new ArrayList<>();
        parametros.add(usuario);
        parametros.add("Pwd123");
        parametros.add(1);
        return parametros;
    }

    private static ArrayList<Object> parametrosUno(Object valor) {
        ArrayList<Object> parametros = new ArrayList<>();
        parametros.add(valor);
        return parametros;
    }

    private int contarUsuarios(String usuario) throws Exception {
        BD bd = BD.nuevaConexion();
        try {
            bd.conectar();
            try (var rs = bd.ejecutarConsulta("SELECT COUNT(*) AS c FROM dbo.Usuarios WHERE Usuario = ?", parametrosUno(usuario))) {
                assertTrue(rs.next());
                return rs.getInt("c");
            }
        } finally {
            bd.desconectar();
        }
    }
}
