package Lugar.Service;

import com.atissosa.cmdb_backend.dto.DTOService;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaAltaServiceTest {
    
    public pruebaAltaServiceTest() {
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
    public void pruebaAltaServiceConDtoNulo() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        Respuesta obtenida = s.altaService(null, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaServiceConTodosLosDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTOService dtoService = new DTOService();
        Respuesta obtenida = s.altaService(dtoService, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaImpresoraConTodosLosDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTOService dtoService = new DTOService();
        dtoService.setActivo(true);
        dtoService.setDescripcion("");
        dtoService.setNombre("");
        
        Respuesta obtenida = s.altaService(dtoService, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaServiceCorrecto() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        Respuesta obtenida = s.altaService(
                (DTOService)FuncionalidadesComunes.crearLugarValido(
                        FuncionalidadesComunes.generarSNrandom(), "SERVICE"), "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(5, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }
    
    @Test
    public void pruebaAltaConServiceExistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        DTOService dtoService = new DTOService();
        dtoService.setActivo(true);
        dtoService.setDescripcion("Descripcion del service");
        dtoService.setDireccion("Direccion del service");
        dtoService.setNombre("Nombre del service");
        
        Respuesta obtenida = s.altaService(dtoService, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(7, dtoService);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
        
        FuncionalidadesComunes.ImprimirFinDeTest();
    }
    
}
