package Lugar.Subagencia;

import com.atissosa.cmdb_backend.dto.DTOLugar;
import com.atissosa.cmdb_backend.dto.DTOSubAgencia;
import com.atissosa.cmdb_backend.dto.Respuesta;
import java.util.ArrayList;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaBuscarSubAgenciaTest {

    public pruebaBuscarSubAgenciaTest() {
    }
    //************ Creo ambiente de testing ***************
    private static Sistema s = EntornoTesting.getInstanciaSistemaTesting();
    //*******************************************************

    @BeforeAll
    public static void crearCarpeta() {
        FuncionalidadesComunes.crearArchivosYCarpetaDeClase();
        FuncionalidadesComunes.iniciarSessionEnSistema(s);
    }

    @AfterAll
    public static void resultadosTest() {
        assertEquals(true, FuncionalidadesComunes.archivosIguales());
    }

    @Test
    public void pruebaConNumeroIgualAcero() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.buscarSubagencias(0);
        Respuesta esperada = Respuesta.nuevaRespuesta(4, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaConNumeroMenorQueCero() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.buscarSubagencias(-18);
        Respuesta esperada = Respuesta.nuevaRespuesta(4, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaConNumeroInexistente() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.buscarSubagencias(9000);
        Respuesta esperada = Respuesta.nuevaRespuesta(12, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaConNumeroExistente() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.buscarSubagencias(500);
        Respuesta esperada = Respuesta.nuevaRespuesta(4, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }
}
