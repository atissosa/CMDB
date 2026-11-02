package Equipo.PC;

import com.atissosa.cmdb_backend.dto.DTOEquipo;
import com.atissosa.cmdb_backend.dto.DTOPC;
import com.atissosa.cmdb_backend.dto.Respuesta;
import java.util.ArrayList;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaModificarPCTest {

    public pruebaModificarPCTest() {
    }
//************ Creo ambiente de testing ***************
    private static Sistema s = EntornoTesting.getInstanciaSistemaTesting();
    //*******************************************************

    @BeforeAll
    public static void crearCarpeta() {
        FuncionalidadesComunes.crearArchivosYCarpetaDeClase();
        FuncionalidadesComunes.iniciarSessionEnSistema(s);
    }

    @BeforeEach
    public void asegurarSesionActiva() {
        FuncionalidadesComunes.iniciarSessionEnSistema(s);
    }

    @AfterAll
    public static void resultadosTest() {
        assertEquals(true, FuncionalidadesComunes.archivosIguales());
    }

    @Test
    public void pruebaModificarPCConDtoNulo() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.modificarPC(null, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaModificarPCConTodosLosDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOPC dtoPC = new DTOPC();
        Respuesta obtenida = s.modificarPC(dtoPC, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaModificarPCConTodosLosDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOPC dtoPC = new DTOPC();
        dtoPC.setCodigo("");
        dtoPC.setDescripcion("");
        dtoPC.setEsPrestamo(false);
        dtoPC.setEstado("");
        dtoPC.setFechaAlta("");
        dtoPC.setId(0);
        dtoPC.setIp("");
        dtoPC.setLugar("");
        dtoPC.setMarca("");
        dtoPC.setMemoriaRam(0);
        dtoPC.setMesesGarantia(0);
        dtoPC.setModelo("");
        dtoPC.setNombreHost("");
        dtoPC.setNumeroSerie("");
        dtoPC.setNumeroPrecinto(0);
        dtoPC.setPrecioDeCompra(0.0);
        dtoPC.setProcesador("");
        dtoPC.setTamanoDisco(0);
        dtoPC.setTipo("");
        dtoPC.setUsuario("");
        dtoPC.setidLugar(0);

        Respuesta obtenida = s.modificarPC(dtoPC, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaModificarConPCInexistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        int indice = 6;
        DTOPC dtoPC = new DTOPC();

        dtoPC.setId(900);
        dtoPC.setDescripcion("Descripcion" + indice);
        dtoPC.setEsPrestamo(false);
        dtoPC.setEstado("ALTA");
        dtoPC.setFechaAlta("2018-07-23");
        dtoPC.setIp("10.120.1." + indice);
        dtoPC.setMarca("HP");
        dtoPC.setMemoriaRam(4);
        dtoPC.setMesesGarantia(36);
        dtoPC.setModelo("Elite Desk 705 G3");
        dtoPC.setNombreHost("PC_" + indice);
        dtoPC.setNumeroSerie("MXL7242PC" + indice);
        dtoPC.setNumeroPrecinto(indice * 111);
        dtoPC.setPrecioDeCompra(800.0);
        dtoPC.setProcesador("AMD A6");
        dtoPC.setTamanoDisco(500);
        dtoPC.setTipo("PC");
        dtoPC.setUsuario("User1");
        dtoPC.setidLugar(1);

        Respuesta obtenida = s.modificarPC(dtoPC, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(8, dtoPC);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaModificarCorrectaPCExistente() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        String sn = FuncionalidadesComunes.generarSNrandom();
        DTOEquipo dtoPc = FuncionalidadesComunes.crearDTOEquipoValido(sn, "PC");
        dtoPc = (DTOPC)s.altaPc((DTOPC) dtoPc).getRetorno();
        dtoPc.setPrecioDeCompra(900.0);
        
        Respuesta obtenida = s.modificarPC((DTOPC) dtoPc, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(6, dtoPc);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

}
