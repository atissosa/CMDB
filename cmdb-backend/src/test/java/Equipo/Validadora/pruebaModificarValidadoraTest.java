package Equipo.Validadora;

import com.atissosa.cmdb_backend.dto.DTOValidadora;
import com.atissosa.cmdb_backend.dto.Respuesta;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaModificarValidadoraTest {

    public pruebaModificarValidadoraTest() {
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
    public void pruebaModificarValidadoraConDtoNulo() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.modificarValidadora(null, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        // Escribimos las salidas en sus correspondientes archivos 
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaModificarValidadoraConTodosLosDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOValidadora dtoValidadora = new DTOValidadora();
        Respuesta obtenida = s.modificarValidadora(dtoValidadora, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaModificarValidadoraConTodosLosDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.modificarValidadora(
                (DTOValidadora)FuncionalidadesComunes.crearEquipoConValoresVacios("VALIDADORA"), "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaModificarConValidadoraInexistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        String sn = FuncionalidadesComunes.generarSNrandom();
        DTOValidadora dtoValidadora = (DTOValidadora)FuncionalidadesComunes.crearDTOEquipoValido(sn, "VALIDADORA");
        Respuesta obtenida = s.modificarValidadora(dtoValidadora, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(8, dtoValidadora);

        // Escribimos las salidas en sus correspondientes archivos  
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaModificarCorrectaValidadoraExistente() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        String sn = FuncionalidadesComunes.generarSNrandom();
        DTOValidadora dtoValidadora = (DTOValidadora)FuncionalidadesComunes.crearDTOEquipoValido(sn, "VALIDADORA");
        dtoValidadora = (DTOValidadora)s.altaValidadora(dtoValidadora).getRetorno();
        dtoValidadora.setFechaAlta("2018-09-26");
        Respuesta obtenida = s.modificarValidadora(dtoValidadora, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(6, dtoValidadora);

        // Escribimos las salidas en sus correspondientes archivos  
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }
}
