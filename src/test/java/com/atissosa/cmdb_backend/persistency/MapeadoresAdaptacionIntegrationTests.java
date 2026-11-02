package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Acceso;
import com.atissosa.cmdb_backend.domain.DepartamentoCentral;
import com.atissosa.cmdb_backend.domain.ItemHistoricoLugar;
import com.atissosa.cmdb_backend.domain.OtroEquipo;
import com.atissosa.cmdb_backend.domain.PlantillaImpresora;
import com.atissosa.cmdb_backend.domain.PlantillaOtroEquipo;
import com.atissosa.cmdb_backend.domain.PlantillaPc;
import com.atissosa.cmdb_backend.domain.PlantillaValidadora;
import com.atissosa.cmdb_backend.domain.Rol;
import com.atissosa.cmdb_backend.domain.Service;
import com.atissosa.cmdb_backend.domain.SubAgencia;
import com.atissosa.cmdb_backend.domain.Usuario;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import com.zaxxer.hikari.HikariDataSource;
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
class MapeadoresAdaptacionIntegrationTests {

    @Autowired
    DataSource dataSource;

    @DynamicPropertySource
    static void configuracionAislada(DynamicPropertyRegistry registry) {
        SqlServerTesting.registrarPropiedades(registry);
        registry.add("cmdb.bd.pool.min-idle", () -> 1);
        registry.add("cmdb.bd.pool.max-size", () -> 3);
        registry.add("cmdb.bd.pool.connection-timeout-ms", () -> 3000L);
        registry.add("cmdb.bd.pool.idle-timeout-ms", () -> 10000L);
        registry.add("cmdb.bd.pool.max-lifetime-ms", () -> 120000L);
        registry.add("cmdb.bd.pool.validation-timeout-ms", () -> 1500L);
    }

    @Test
    void cubreLecturasRepresentativasPorFamiliaDeMapeadores() throws Exception {
        assertInstanceOf(HikariDataSource.class, dataSource);
        assertTrue(BD.tieneDataSourceConfigurado());

        MapeadorMarcas mapeadorMarcas = MapeadorMarcas.nuevoMapeador();
        assertNotNull(mapeadorMarcas);
        assertTrue(mapeadorMarcas.sqlBuscar().contains("VMarcas"));
        assertFalse(Persistencia.getInstancia().buscar(MapeadorDepartamentoCentral.nuevoMapeador(new DepartamentoCentral())).isEmpty());
        assertFalse(Persistencia.getInstancia().buscar(MapeadorService.nuevoMapeador(new Service())).isEmpty());
        assertFalse(Persistencia.getInstancia().buscar(MapeadorSubAgencia.nuevoMapeador(new SubAgencia())).isEmpty());

        assertNotNull(Persistencia.getInstancia().buscar(MapeadorPC.nuevoMapeador(new DTOFiltroEquipo())));
        assertNotNull(Persistencia.getInstancia().buscar(MapeadorImpresora.nuevoMapeador(new DTOFiltroEquipo())));
        assertNotNull(Persistencia.getInstancia().buscar(MapeadorValidadora.nuevoMapeador(new DTOFiltroEquipo())));
        assertNotNull(Persistencia.getInstancia().buscar(MapeadorOtroEquipo.nuevoMapeador(new DTOFiltroEquipo())));

        assertNotNull(Persistencia.getInstancia().buscar(MapeadorPlantillaPc.nuevoMapeador(new PlantillaPc())));
        assertNotNull(Persistencia.getInstancia().buscar(MapeadorPlantillaImpresora.nuevoMapeador(new PlantillaImpresora())));
        assertNotNull(Persistencia.getInstancia().buscar(MapeadorPlantillaValidadora.nuevoMapeador(new PlantillaValidadora())));
        assertNotNull(Persistencia.getInstancia().buscar(MapeadorPlantillaOtroEquipo.nuevoMapeador(new PlantillaOtroEquipo())));

        assertFalse(Persistencia.getInstancia().buscar(MapeadorUsuario.nuevoMapeador(new Usuario())).isEmpty());
        assertFalse(Persistencia.getInstancia().buscar(MapeadorAcceso.nuevoMapeador(new Acceso())).isEmpty());
        assertNotNull(Persistencia.getInstancia().buscar(MapeadorItemHistoricoLugar.nuevoMapeador(new ItemHistoricoLugar())));
    }

    @Test
    void conservaBusquedasVaciasYTratamientoDeNull() throws Exception {
        DepartamentoCentral departamento = new DepartamentoCentral();
        departamento.setId(-1);
        assertTrue(Persistencia.getInstancia().buscar(MapeadorDepartamentoCentral.nuevoMapeador(departamento)).isEmpty());

        Usuario usuario = new Usuario();
        usuario.setUsuario("usuario_inexistente_" + UUID.randomUUID());
        assertTrue(Persistencia.getInstancia().buscar(MapeadorUsuario.nuevoMapeador(usuario)).isEmpty());

        PlantillaPc plantillaPc = new PlantillaPc();
        plantillaPc.setMarca("MarcaNoExistente" + UUID.randomUUID());
        assertTrue(Persistencia.getInstancia().buscar(MapeadorPlantillaPc.nuevoMapeador(plantillaPc)).isEmpty());

        Acceso acceso = new Acceso();
        acceso.setOperacion("OPERACION_INEXISTENTE");
        Rol rol = new Rol();
        rol.setId(-1);
        acceso.setRol(rol);
        assertTrue(Persistencia.getInstancia().buscar(MapeadorAcceso.nuevoMapeador(acceso)).isEmpty());

        PlantillaOtroEquipo plantillaOtro = new PlantillaOtroEquipo();
        plantillaOtro.setTipo(OtroEquipo.TiposEquipo.ROUTER);
        assertNotNull(Persistencia.getInstancia().buscar(MapeadorPlantillaOtroEquipo.nuevoMapeador(plantillaOtro)));
    }

    @Test
    void conservaClaveGeneradaYRollbackEnOperacionCompuesta() throws Exception {
        String nombreCommit = "F15-CM-" + UUID.randomUUID().toString().substring(0, 8);
        DepartamentoCentral commit = new DepartamentoCentral();
        commit.setNombre(nombreCommit);
        commit.setDescripcion("alta commit f15");
        commit.setActivo(true);
        int id = Persistencia.getInstancia().insertarSP(MapeadorDepartamentoCentral.nuevoMapeador(commit));
        assertTrue(id > 0);

        DepartamentoCentral filtro = new DepartamentoCentral();
        filtro.setId(id);
        ArrayList<?> hallados = Persistencia.getInstancia().buscar(MapeadorDepartamentoCentral.nuevoMapeador(filtro));
        assertEquals(1, hallados.size());

        DepartamentoCentral baja = new DepartamentoCentral();
        baja.setId(id);
        assertEquals(1, Persistencia.getInstancia().eliminar(MapeadorDepartamentoCentral.nuevoMapeador(baja)));

        String nombreRollback = "F15-RB-" + UUID.randomUUID().toString().substring(0, 8);
        DepartamentoCentral rollbackDepto = new DepartamentoCentral();
        rollbackDepto.setNombre(nombreRollback);
        rollbackDepto.setDescripcion("alta rollback f15");
        rollbackDepto.setActivo(true);
        MapeadorDepartamentoCentral mapper = MapeadorDepartamentoCentral.nuevoMapeador(rollbackDepto);

        BD bd = BD.nuevaConexion();
        try {
            bd.conectar(true);
            int provisionalId = bd.ejecutarProcedimiento(mapper.sqlInsertar(), mapper.parametrosInsertar());
            assertTrue(provisionalId > 0);
            bd.rollback();
        } finally {
            bd.desconectar();
        }

        DepartamentoCentral filtroRollback = new DepartamentoCentral();
        filtroRollback.setNombre(nombreRollback);
        assertTrue(Persistencia.getInstancia().buscar(MapeadorDepartamentoCentral.nuevoMapeador(filtroRollback)).isEmpty());
    }

    @Test
    void conservaContratoDeExcepcionAnteErrorDePersistencia() {
        ExcepcionConexion ex = assertThrows(ExcepcionConexion.class,
                () -> Persistencia.getInstancia().insertarSP(MapeadorUsuario.nuevoMapeador(new Usuario())));
        assertNotNull(ex.getMessage());
    }
}
