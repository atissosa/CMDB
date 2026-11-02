package Usuario.login;

import com.atissosa.cmdb_backend.dto.DTOUsuario;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaLoginTest {

    public pruebaLoginTest() {
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
    public void pruebaConDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.login(null, null);
        Respuesta esperada = Respuesta.nuevaRespuesta(2, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaConDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.login("", "");
        Respuesta esperada = Respuesta.nuevaRespuesta(2, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaConUsuarioInexistente() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.login("pablito", "pablito");
        Respuesta esperada = Respuesta.nuevaRespuesta(2, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaConUsuarioExistenteYcontraseñaIncorrecta() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        s.logout("user1");
        
        Respuesta obtenida = s.login("user1", "pablito");
        Respuesta esperada = Respuesta.nuevaRespuesta(2, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaConUsuarioYcontraseñaCorrectos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        s.logout("user1");
        
        Respuesta obtenida = s.login("user1", "pass.1234");
        Respuesta esperada = Respuesta.nuevaRespuesta(1, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
    }
}
