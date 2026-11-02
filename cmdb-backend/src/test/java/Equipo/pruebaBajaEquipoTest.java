package Equipo;

import com.atissosa.cmdb_backend.dto.DTOEquipo;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.dto.DTOPC;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaBajaEquipoTest {

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

    @AfterEach
    public void resultadosTest(){
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
        Respuesta obtenida = s.darEquipoDeBaja(null);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
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
        Respuesta obtenida = s.darEquipoDeBaja(dtoFiltroEquipo);
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
        Respuesta obtenida = s.darEquipoDeBaja(dtoFiltroEquipo);
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
        dtoFiltroEquipo.agregarNumeroSerie("aaaaa");
        dtoFiltroEquipo.agregarUsuario("User1");
        Respuesta obtenida = s.darEquipoDeBaja(dtoFiltroEquipo);
        Respuesta esperada = Respuesta.nuevaRespuesta(10, dtoFiltroEquipo);
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
        DTOEquipo dtoPc = FuncionalidadesComunes.crearDTOEquipoValido(sn, "PC");
        s.altaPc((DTOPC) dtoPc);
        dtoFiltroEquipo.agregarNumeroSerie(sn);
        dtoFiltroEquipo.agregarUsuario("user1");
        Respuesta obtenida = s.darEquipoDeBaja(dtoFiltroEquipo);
        Respuesta esperada = Respuesta.nuevaRespuesta(9, dtoFiltroEquipo);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        

        FuncionalidadesComunes.ImprimirFinDeTest();

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }
}
