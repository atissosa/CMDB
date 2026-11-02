package Lugar.DepartamentoCentral;

import com.atissosa.cmdb_backend.dto.DTOLugar;
import com.atissosa.cmdb_backend.dto.Respuesta;
import java.util.ArrayList;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaBuscarDepartamentoCentralTest {
    
    public pruebaBuscarDepartamentoCentralTest() {
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
    public void pruebaConIdIgualAcero() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        

        Respuesta obtenida = s.buscarDepartamentosCentral(0);
        Respuesta esperada = Respuesta.nuevaRespuesta(4, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaConIdMenorQueCero() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        

        Respuesta obtenida = s.buscarDepartamentosCentral(-18);
        Respuesta esperada = Respuesta.nuevaRespuesta(4, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaConIdInexistente() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        

        Respuesta obtenida = s.buscarDepartamentosCentral(5000);
        Respuesta esperada = Respuesta.nuevaRespuesta(12, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaConIdExistente() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        

        Respuesta obtenida = s.buscarDepartamentosCentral(1);
        Respuesta esperada = Respuesta.nuevaRespuesta(4, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }
}
