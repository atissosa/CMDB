package Lugar.DepartamentoCentral;

import com.atissosa.cmdb_backend.dto.DTODepartamentoCentral;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaAltaDepartamentoCentralTest {
    
    public pruebaAltaDepartamentoCentralTest() {
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
    public void pruebaAltaDepartamentoConDtoNulo() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        Respuesta obtenida = s.altaDepartamentoCentral(null, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaDepartamentoConTodosLosDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTODepartamentoCentral dtoDepartamento = new DTODepartamentoCentral();
        Respuesta obtenida = s.altaDepartamentoCentral(dtoDepartamento, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaDepartamentoConTodosLosDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTODepartamentoCentral dtoDepartamento = new DTODepartamentoCentral();
        dtoDepartamento.setActivo(true);
        dtoDepartamento.setDescripcion("");
        dtoDepartamento.setNombre("");
        
        Respuesta obtenida = s.altaDepartamentoCentral(dtoDepartamento, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaDepartamentoCorrecto() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        Respuesta obtenida = s.altaDepartamentoCentral(
                (DTODepartamentoCentral)FuncionalidadesComunes.crearLugarValido(
                        FuncionalidadesComunes.generarSNrandom(), "DEPARTAMENTO"), "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(5, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }
    
    @Test
    public void pruebaAltaConDepartamentoExistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTODepartamentoCentral dtoDepartamento = new DTODepartamentoCentral();
        dtoDepartamento.setActivo(true);
        dtoDepartamento.setDescripcion("Descripcion del depatamento");
        dtoDepartamento.setNombre("Nombre del depatamento");
        
        Respuesta obtenida = s.altaDepartamentoCentral(dtoDepartamento, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(7, dtoDepartamento);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }    
}
