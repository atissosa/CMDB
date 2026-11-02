package Equipo;

import com.atissosa.cmdb_backend.dto.DTOEquipo;
import com.atissosa.cmdb_backend.dto.DTOImpresora;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaDevolverEquipoACentralTest {

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
        Respuesta obtenida = s.devolverEquipoCentral(null, null);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida );
        

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
        DTOEquipo equipo = FuncionalidadesComunes.crearEquipoConTodosLosValoresNulos("PC");
        Respuesta obtenida = s.devolverEquipoCentral(equipo, null);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida );
        

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
        Respuesta obtenida = s.devolverEquipoCentral(
                FuncionalidadesComunes.crearEquipoConValoresVacios("IMPRESORA"), "");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida );
        

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
        
        Respuesta obtenida = s.devolverEquipoCentral(
                FuncionalidadesComunes.crearDTOEquipoValido("aaaa", "VALIDADORA"), "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(15, null);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida );
        

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
        DTOEquipo dtoImpresora = FuncionalidadesComunes.crearDTOEquipoValido(sn, "IMPRESORA");
        dtoImpresora.setEsPrestamo(true);
        s.altaImpresora((DTOImpresora) dtoImpresora);
        s.despacharEquipo(dtoImpresora, "user1");
        s.prestarEquipoSubagencia(dtoImpresora, "user1");
        Respuesta obtenida = s.devolverEquipoCentral(dtoImpresora, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(17, dtoImpresora);
        /**
         * ******************************************************************
         */

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida );
        

        FuncionalidadesComunes.ImprimirFinDeTest();

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }
}
