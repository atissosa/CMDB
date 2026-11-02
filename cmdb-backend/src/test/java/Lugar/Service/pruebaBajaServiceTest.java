package Lugar.Service;

import com.atissosa.cmdb_backend.dto.DTOService;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaBajaServiceTest {
    
    public pruebaBajaServiceTest() {
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
    public void pruebaBajaServiceConDtoNulo() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        Respuesta obtenida = s.bajaService(null, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaBajaServiceConTodosLosDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTOService dtoService = new DTOService();
        Respuesta obtenida = s.bajaService(dtoService, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaBajaServiceConTodosLosDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTOService dtoService = new DTOService();
        dtoService.setActivo(true);
        dtoService.setDescripcion("");
        dtoService.setDireccion("");
        dtoService.setNombre("");
        
        Respuesta obtenida = s.bajaService(dtoService, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }
    
    @Test
    public void pruebaBajaConServiceInexistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTOService dtoService = new DTOService();
        dtoService.setActivo(true);
        dtoService.setDescripcion("Descripcion del service");
        dtoService.setNombre("Nombre del service");
        dtoService.setDireccion("Direccion del servicio");
        dtoService.setId(900);
        
        Respuesta obtenida = s.bajaService(dtoService, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(10, dtoService);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }
    
    @Test
    public void pruebaBajaConServiceExistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTOService dtoService = new DTOService();
        dtoService.setActivo(true);
        dtoService.setDescripcion("Descripcion del service");
        dtoService.setNombre("Nombre del service");
        dtoService.setDireccion("Direccion del servicio");
        dtoService.setId(1);
        
        Respuesta obtenida = s.bajaService(dtoService, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(9, dtoService);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }
    
}
