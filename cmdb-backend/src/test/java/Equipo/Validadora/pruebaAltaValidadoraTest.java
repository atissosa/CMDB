package Equipo.Validadora;

import com.atissosa.cmdb_backend.dto.DTOValidadora;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaAltaValidadoraTest {

    public pruebaAltaValidadoraTest() {
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
    public void pruebaAltaValidadoraConDtoNulo() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.altaValidadora(null);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaValidadoraConTodosLosDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOValidadora dtoValidadora = new DTOValidadora();
        Respuesta obtenida = s.altaValidadora(dtoValidadora);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaValidadoraConTodosLosDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOValidadora dtoValidadora = new DTOValidadora();
        dtoValidadora.setCodigo("");
        dtoValidadora.setDescripcion("");
        dtoValidadora.setEsPrestamo(false);
        dtoValidadora.setEstado("");
        dtoValidadora.setFechaAlta("");
        dtoValidadora.setId(0);
        dtoValidadora.setLugar("");
        dtoValidadora.setMarca("");
        dtoValidadora.setMesesGarantia(0);
        dtoValidadora.setModelo("");
        dtoValidadora.setNumeroSerie("");
        dtoValidadora.setNumeroPrecinto(0);
        dtoValidadora.setPrecioDeCompra(0.0);
        dtoValidadora.setTipo("");
        dtoValidadora.setUsuario("");
        dtoValidadora.setidLugar(0);
        Respuesta obtenida = s.altaValidadora(dtoValidadora);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaValidadoraCorrecta() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        String sn = FuncionalidadesComunes.generarSNrandom();
        DTOValidadora dtoValidadora = (DTOValidadora)FuncionalidadesComunes.crearDTOEquipoValido(sn, "VALIDADORA");
        Respuesta obtenida = s.altaValidadora(dtoValidadora);
        Respuesta esperada = Respuesta.nuevaRespuesta(5, dtoValidadora);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaConValidadoraExistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();
        
        String sn = FuncionalidadesComunes.generarSNrandom();
        DTOValidadora dtoValidadora = (DTOValidadora)FuncionalidadesComunes.crearDTOEquipoValido(sn, "VALIDADORA");
        s.altaValidadora(dtoValidadora);
        Respuesta obtenida = s.altaValidadora(dtoValidadora);
        Respuesta esperada = Respuesta.nuevaRespuesta(7, dtoValidadora);
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        assertEquals(esperada.getCodigo(), obtenida.getCodigo());

        FuncionalidadesComunes.ImprimirFinDeTest();
    }

}
