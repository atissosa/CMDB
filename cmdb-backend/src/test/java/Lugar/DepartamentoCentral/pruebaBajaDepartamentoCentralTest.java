package Lugar.DepartamentoCentral;

import com.atissosa.cmdb_backend.dto.DTODepartamentoCentral;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaBajaDepartamentoCentralTest {
    
    public pruebaBajaDepartamentoCentralTest() {
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
    public void pruebaBajaDepartamentoConDtoNulo() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        Respuesta obtenida = s.bajaDepartamentoCentral(null, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaBajaDepartamentoConTodosLosDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTODepartamentoCentral dtoDepartamento = new DTODepartamentoCentral();
        Respuesta obtenida = s.bajaDepartamentoCentral(dtoDepartamento, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaBajaDepartamentoConTodosLosDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTODepartamentoCentral dtoDepartamento = new DTODepartamentoCentral();
        dtoDepartamento.setActivo(true);
        dtoDepartamento.setDescripcion("");
        dtoDepartamento.setNombre("");
        
        Respuesta obtenida = s.bajaDepartamentoCentral(dtoDepartamento, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }
    
    @Test
    public void pruebaBajaConDepartamentoInexistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTODepartamentoCentral dtoDepartamento = new DTODepartamentoCentral();
        dtoDepartamento.setActivo(true);
        dtoDepartamento.setDescripcion("Descripcion del depatamento");
        dtoDepartamento.setNombre("Nombre del depatamento");
        dtoDepartamento.setId(900);
        
        Respuesta obtenida = s.bajaDepartamentoCentral(dtoDepartamento, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(10, dtoDepartamento);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }
    
    @Test
    public void pruebaBajaConDepartamentoExistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTODepartamentoCentral dtoDepartamento = new DTODepartamentoCentral();
        dtoDepartamento.setActivo(true);
        dtoDepartamento.setDescripcion("Descripcion del depatamento");
        dtoDepartamento.setNombre("Nombre del depatamento");
        dtoDepartamento.setId(1);
        
        Respuesta obtenida = s.bajaDepartamentoCentral(dtoDepartamento, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(9, dtoDepartamento);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }
    
}
