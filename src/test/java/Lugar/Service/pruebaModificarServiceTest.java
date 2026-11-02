package Lugar.Service;

import com.atissosa.cmdb_backend.dto.DTOService;
import com.atissosa.cmdb_backend.dto.Respuesta;
import java.util.ArrayList;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaModificarServiceTest {

    public pruebaModificarServiceTest() {
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
    public void pruebaModificarServiceConDtoNulo() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.modificarService(null, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaModificarServiceConTodosLosDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOService dtoService = new DTOService();
        Respuesta obtenida = s.modificarService(dtoService, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaModificarServiceConTodosLosDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOService dtoService = new DTOService();
        dtoService.setActivo(true);
        dtoService.setDescripcion("");
        dtoService.setDireccion("");
        dtoService.setNombre("");

        Respuesta obtenida = s.modificarService(dtoService, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaModificarConServiceInexistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOService dtoService = new DTOService();
        dtoService.setActivo(true);
        dtoService.setDescripcion("Descripcion del depatamento");
        dtoService.setNombre("Nombre del depatamento");
        dtoService.setDireccion("Direccion del servicio");
        dtoService.setId(900);

        Respuesta obtenida = s.modificarService(dtoService, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(8, dtoService);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaModificarConServiceExistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOService dtoService = (DTOService)FuncionalidadesComunes.crearLugarValido(
                FuncionalidadesComunes.generarSNrandom(), "SERVICE");
        dtoService = (DTOService)s.altaService(dtoService, "user1").getRetorno();
        
        
        Respuesta obtenida = s.modificarService(dtoService, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(6, dtoService);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

}
