package Equipo.Impresora;

import com.atissosa.cmdb_backend.dto.DTOImpresora;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaModificarImpresoraTest {

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
        Respuesta obtenida = s.modificarImpresora(null, null);
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
        DTOImpresora dtoImpresora = new DTOImpresora();
        Respuesta obtenida = s.modificarImpresora(dtoImpresora, null);
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
        Respuesta obtenida = s.modificarImpresora(
                (DTOImpresora) FuncionalidadesComunes.crearEquipoConTodosLosValoresNulos("IMPRESORA"), "");
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
        Respuesta obtenida = s.modificarImpresora(
                (DTOImpresora) FuncionalidadesComunes.crearDTOEquipoValido("aaaa", "IMPRESORA"), "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(8, null);
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
        s.altaImpresora(
                (DTOImpresora) FuncionalidadesComunes.crearDTOEquipoValido("impresoraNueva", "IMPRESORA"));
        DTOImpresora impExistente = (DTOImpresora)s.buscarEquipo("impresoraNueva").getRetorno();
        Respuesta obtenida = s.modificarImpresora(impExistente, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(6, null);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }
}
