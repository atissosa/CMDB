package Equipo;

import com.atissosa.cmdb_backend.dto.DTOEquipo;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.dto.DTOImpresora;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaAsignarEquipoParaPrestamoTest {

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
        Respuesta obtenida = s.asignarEquipoParaPrestamo(null);
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
        DTOFiltroEquipo dtoFiltroEquipo = new DTOFiltroEquipo();
        Respuesta obtenida = s.asignarEquipoParaPrestamo(dtoFiltroEquipo);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, dtoFiltroEquipo);
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
        DTOFiltroEquipo dtoFiltroEquipo = new DTOFiltroEquipo();
        dtoFiltroEquipo.agregarNumeroSerie("");
        dtoFiltroEquipo.agregarUsuario("");
        Respuesta obtenida = s.asignarEquipoParaPrestamo(dtoFiltroEquipo);
        Respuesta esperada = Respuesta.nuevaRespuesta(14, dtoFiltroEquipo);
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
        DTOFiltroEquipo dtoFiltroEquipo = new DTOFiltroEquipo();
        dtoFiltroEquipo.agregarNumeroSerie(FuncionalidadesComunes.generarSNrandom());
        dtoFiltroEquipo.agregarUsuario("User1");
        Respuesta obtenida = s.asignarEquipoParaPrestamo(dtoFiltroEquipo);
        Respuesta esperada = Respuesta.nuevaRespuesta(8, dtoFiltroEquipo);
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
        DTOFiltroEquipo dtoFiltroEquipo = new DTOFiltroEquipo();
        String sn = FuncionalidadesComunes.generarSNrandom();
        DTOEquipo dtoImpresora = FuncionalidadesComunes.crearDTOEquipoValido(sn, "IMPRESORA");
        s.altaImpresora((DTOImpresora) dtoImpresora);
        s.despacharEquipo(dtoImpresora, "user1");
        dtoFiltroEquipo.agregarNumeroSerie(sn);
        dtoFiltroEquipo.agregarUsuario("user1");
        Respuesta obtenida = s.asignarEquipoParaPrestamo(dtoFiltroEquipo);
        Respuesta esperada = Respuesta.nuevaRespuesta(17, dtoFiltroEquipo);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }
}
