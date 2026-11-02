package Lugar.DepartamentoCentral;

import com.atissosa.cmdb_backend.dto.DTODepartamentoCentral;
import com.atissosa.cmdb_backend.dto.Respuesta;
import java.util.ArrayList;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaModificarDepartamentoCentralTest {

    public pruebaModificarDepartamentoCentralTest() {
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
    public void pruebaModificarDepartamentoCentralConDtoNulo() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        Respuesta obtenida = s.modificarDepartamentoCentral(null, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaModificarDepartamentoCentralConTodosLosDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTODepartamentoCentral dtoDepartamentoCentral = new DTODepartamentoCentral();
        Respuesta obtenida = s.modificarDepartamentoCentral(dtoDepartamentoCentral, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaModificarDepartamentoCentralConTodosLosDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTODepartamentoCentral dtoDepartamentoCentral = new DTODepartamentoCentral();
        dtoDepartamentoCentral.setActivo(true);
        dtoDepartamentoCentral.setDescripcion("");
        dtoDepartamentoCentral.setNombre("");

        Respuesta obtenida = s.modificarDepartamentoCentral(dtoDepartamentoCentral, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaModificarConDepartamentoCentralInexistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTODepartamentoCentral dtoDepartamentoCentral = new DTODepartamentoCentral();
        dtoDepartamentoCentral.setActivo(true);
        dtoDepartamentoCentral.setDescripcion("Descripcion del depatamento");
        dtoDepartamentoCentral.setNombre("Nombre del depatamento");
        dtoDepartamentoCentral.setId(900);

        Respuesta obtenida = s.modificarDepartamentoCentral(dtoDepartamentoCentral, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(8, dtoDepartamentoCentral);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaModificarConDepartamentoCentralExistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTODepartamentoCentral dtoDepartamentoCentral = new DTODepartamentoCentral();
        dtoDepartamentoCentral.setActivo(true);
        dtoDepartamentoCentral.setDescripcion("Descripcion del departamento");
        dtoDepartamentoCentral.setNombre("Nombre del departamento");
        dtoDepartamentoCentral.setId(50);

        Respuesta obtenida = s.modificarDepartamentoCentral(dtoDepartamentoCentral, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(6, dtoDepartamentoCentral);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }
}
