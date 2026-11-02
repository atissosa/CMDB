package Equipo.OtroEquipo;

import com.atissosa.cmdb_backend.dto.DTOOtroEquipo;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaAltaOtroEquipoTest {

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
    public void pruebaAltaOtroEquipoConDtoNulo() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.altaOtroEquipo(null);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaAltaOtroEquipoConTodosLosDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOOtroEquipo dtoOtroEquipo = new DTOOtroEquipo();
        Respuesta obtenida = s.altaOtroEquipo(dtoOtroEquipo);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaAltaOtroEquipoConTodosLosDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOOtroEquipo dtoOtroEquipo = new DTOOtroEquipo();
        dtoOtroEquipo.setCodigo("");
        dtoOtroEquipo.setDescripcion("");
        dtoOtroEquipo.setEsPrestamo(false);
        dtoOtroEquipo.setEstado("");
        dtoOtroEquipo.setFechaAlta("");
        dtoOtroEquipo.setId(0);
        dtoOtroEquipo.setLugar("");
        dtoOtroEquipo.setMarca("");
        dtoOtroEquipo.setMesesGarantia(0);
        dtoOtroEquipo.setModelo("");
        dtoOtroEquipo.setNumeroSerie("");
        dtoOtroEquipo.setNumeroPrecinto(0);
        dtoOtroEquipo.setPrecioDeCompra(0.0);
        dtoOtroEquipo.setTipo("");
        dtoOtroEquipo.setUsuario("");
        dtoOtroEquipo.setidLugar(0);
        Respuesta obtenida = s.altaOtroEquipo(dtoOtroEquipo);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaAltaOtroEquipoCorrecta() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        String sn = FuncionalidadesComunes.generarSNrandom();
        DTOOtroEquipo dtoOtroEquipo = (DTOOtroEquipo)FuncionalidadesComunes.crearDTOEquipoValido(sn, "SCANNER");
        Respuesta obtenida = s.altaOtroEquipo(dtoOtroEquipo);
        Respuesta esperada = Respuesta.nuevaRespuesta(5, dtoOtroEquipo);

        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaAltaConOtroEquipoExistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        String sn = FuncionalidadesComunes.generarSNrandom();
        DTOOtroEquipo dtoOtroEquipo = (DTOOtroEquipo)FuncionalidadesComunes.crearDTOEquipoValido(sn, "MODEM");
        s.altaOtroEquipo(dtoOtroEquipo);
        Respuesta obtenida = s.altaOtroEquipo(dtoOtroEquipo);
        Respuesta esperada = Respuesta.nuevaRespuesta(7, dtoOtroEquipo);

        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);

        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

}
