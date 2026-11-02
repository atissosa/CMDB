package Equipo;

import com.atissosa.cmdb_backend.dto.DTOEquipo;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.dto.DTOImpresora;
import com.atissosa.cmdb_backend.dto.DTOPC;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaAsociarPrestamoAlEquipoRotoTest {

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
    public void pruebaAsociarEquipoRotoConPrestamo() {
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
        // Creo el equipo de prestamo
        String snPrest = FuncionalidadesComunes.generarSNrandom();
        DTOEquipo dtoPCpres = FuncionalidadesComunes.crearDTOEquipoValido(snPrest, "PC");
        dtoPCpres.setEsPrestamo(true);
        s.altaPc((DTOPC) dtoPCpres);
        s.despacharEquipo(dtoPCpres, "user1");

        //creo el equipo que se va a romper
        String snRoto = FuncionalidadesComunes.generarSNrandom();
        DTOEquipo dtoPCroto = FuncionalidadesComunes.crearDTOEquipoValido(snRoto, "PC");
        dtoPCroto.setidLugar(47);
        s.altaPc((DTOPC) dtoPCroto);
        s.despacharEquipo(dtoPCroto, "user1");

        Respuesta obtenida = s.enviarEquipoAReparar(dtoPCroto, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(17, null);
        /**
         * ******************************************************************
         */
        
        dtoPCpres.setidLugarActual(47);
        dtoPCpres.setNumeroSerieEquipoPrestamo(snRoto);
        s.prestarEquipoSubagencia(dtoPCpres, "user1");

        dtoPCroto = (DTOEquipo) s.buscarEquipo(snRoto).getRetorno();

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        assertEquals(snPrest, dtoPCroto.getNumeroSerieEquipoPrestamo());

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
        // Creo el equipo de prestamo
        String snPrest = FuncionalidadesComunes.generarSNrandom();
        DTOEquipo dtoPCpres = FuncionalidadesComunes.crearDTOEquipoValido(snPrest, "PC");
        dtoPCpres.setEsPrestamo(true);
        s.altaPc((DTOPC) dtoPCpres);
        s.despacharEquipo(dtoPCpres, "user1");

        //creo el equipo que se va a romper
        String snRoto = FuncionalidadesComunes.generarSNrandom();
        DTOEquipo dtoPCroto = FuncionalidadesComunes.crearDTOEquipoValido(snRoto, "PC");
        dtoPCroto.setidLugar(9);
        s.altaPc((DTOPC) dtoPCroto);
        s.despacharEquipo(dtoPCroto, "user1");

        Respuesta obtenida = s.enviarEquipoAReparar(dtoPCroto, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(17, null);

        dtoPCpres.setidLugarActual(9);
        dtoPCpres.setNumeroSerieEquipoPrestamo(snRoto);
        s.prestarEquipoSubagencia(dtoPCpres, "user1");
        /**
         * ******************************************************************
         */

        dtoPCpres = (DTOEquipo) s.buscarEquipo(snPrest).getRetorno();

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        assertEquals(snRoto, dtoPCpres.getNumeroSerieEquipoPrestamo());
    }

    @Test
    public void pruebaRecuperarEquipoAutomaticamenteCuandoElEquipoRotoSeRepara() {
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
        // Creo el equipo de prestamo
        String snPrest = FuncionalidadesComunes.generarSNrandom();
        DTOEquipo dtoPCpres = FuncionalidadesComunes.crearDTOEquipoValido(snPrest, "PC");
        dtoPCpres.setEsPrestamo(true);
        s.altaPc((DTOPC) dtoPCpres);
        s.despacharEquipo(dtoPCpres, "user1");

        //creo el equipo que se va a romper
        String snRoto = FuncionalidadesComunes.generarSNrandom();
        DTOEquipo dtoPCroto = FuncionalidadesComunes.crearDTOEquipoValido(snRoto, "PC");
        dtoPCroto.setidLugar(80);
        s.altaPc((DTOPC) dtoPCroto);
        s.despacharEquipo(dtoPCroto, "user1");

        Respuesta obtenida = s.enviarEquipoAReparar(dtoPCroto, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(17, null);

        dtoPCpres.setidLugarActual(80);
        dtoPCpres.setNumeroSerieEquipoPrestamo(snRoto);
        s.prestarEquipoSubagencia(dtoPCpres, "user1");
        /**
         * ******************************************************************
         */
        
        s.ingresarEquipoAReparar(dtoPCroto, "user1");
        s.devolverEquipoReparado(dtoPCroto, "user1");

        dtoPCpres = (DTOEquipo) s.buscarEquipo(snPrest).getRetorno();

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        assertEquals("EN_RECUPERACION", dtoPCpres.getEstado());
    }
}
