package Equipo;

import com.atissosa.cmdb_backend.dto.DTOEquipo;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.dto.DTOOtroEquipo;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaFinalizarReparacionTest {

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
    public void pruebaConDTONulo() {
        /**
         * ************** CREACION DE ARCHIVOS*********************
         */

        FuncionalidadesComunes.ImprimirComienzoDeTest();
        /**
         * *******************************************************
         */

        /**
         * ** ESTO ES LO QUE CAMBIA EN CADA TEST ***************************
         */
        Respuesta obtenida = s.devolverEquipoReparado(null, null);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

    }

    @Test
    public void pruebaConDatosNulos() {
        /**
         * ************** CREACION DE ARCHIVOS*********************
         */

        FuncionalidadesComunes.ImprimirComienzoDeTest();
        /**
         * *******************************************************
         */

        /**
         * ** ESTO ES LO QUE CAMBIA EN CADA TEST ***************************
         */
        Respuesta obtenida = s.devolverEquipoReparado(
                FuncionalidadesComunes.crearEquipoConTodosLosValoresNulos("SCANNER"), null);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

    }

    @Test
    public void pruebaConValoresVacios() {
        /**
         * ************** CREACION DE ARCHIVOS*********************
         */

        FuncionalidadesComunes.ImprimirComienzoDeTest();
        /**
         * *******************************************************
         */
        /**
         * ** ESTO ES LO QUE CAMBIA EN CADA TEST ***************************
         */
        Respuesta obtenida = s.devolverEquipoReparado(
                FuncionalidadesComunes.crearEquipoConValoresVacios("ROUTER"), "");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

    }

    @Test
    public void pruebaConEquipoInexistente() {
        /**
         * ************** CREACION DE ARCHIVOS*********************
         */

        FuncionalidadesComunes.ImprimirComienzoDeTest();
        /**
         * *******************************************************
         */
        /**
         * ** ESTO ES LO QUE CAMBIA EN CADA TEST ***************************
         */
        Respuesta obtenida = s.devolverEquipoReparado(
                FuncionalidadesComunes.crearDTOEquipoValido("aaaa", "IMPRESORA"), "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(15, null);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

    }

    @Test
    public void pruebaConEquipoExistente() {
        /**
         * ************** CREACION DE ARCHIVOS*********************
         */

        FuncionalidadesComunes.ImprimirComienzoDeTest();
        /**
         * *******************************************************
         */
        /**
         * ** ESTO ES LO QUE CAMBIA EN CADA TEST ***************************
         */
        String sn = FuncionalidadesComunes.generarSNrandom();
        DTOEquipo dtoOtroEquipo = FuncionalidadesComunes.crearDTOEquipoValido(sn, "SCANNER");
        dtoOtroEquipo.setidLugar(47);
        s.altaOtroEquipo((DTOOtroEquipo) dtoOtroEquipo);
        s.enviarEquipoAReparar(dtoOtroEquipo, "user1");
        s.ingresarEquipoAReparar(dtoOtroEquipo, "user1");
        Respuesta obtenida = s.devolverEquipoReparado(dtoOtroEquipo, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(17, dtoOtroEquipo);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

    }
}
