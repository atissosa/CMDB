package Equipo.OtroEquipo;

import com.atissosa.cmdb_backend.dto.DTOEquipo;
import com.atissosa.cmdb_backend.dto.DTOOtroEquipo;
import com.atissosa.cmdb_backend.dto.Respuesta;
import java.util.ArrayList;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaModificarOtroEquipoTest {

    public pruebaModificarOtroEquipoTest() {
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
    public void pruebaModificarOtroEquipoConDtoNulo() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.modificarOtroEquipo(null, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        // Escribimos las salidas en sus correspondientes archivos 
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaModificarOtroEquipoConTodosLosDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOOtroEquipo dtoOtroEquipo = new DTOOtroEquipo();
        Respuesta obtenida = s.modificarOtroEquipo(dtoOtroEquipo, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaModificarOtroEquipoConTodosLosDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOOtroEquipo dtoOtroEquipo = new DTOOtroEquipo();
        dtoOtroEquipo.setDescripcion("");
        dtoOtroEquipo.setEsPrestamo(false);
        dtoOtroEquipo.setEstado("");
        dtoOtroEquipo.setFechaAlta("");

        dtoOtroEquipo.setMarca("");
        dtoOtroEquipo.setMesesGarantia(0);
        dtoOtroEquipo.setModelo("");

        dtoOtroEquipo.setNumeroSerie("");
        dtoOtroEquipo.setNumeroPrecinto(0);
        dtoOtroEquipo.setPrecioDeCompra(0.0);
        dtoOtroEquipo.setTipo("");
        dtoOtroEquipo.setUsuario("");
        dtoOtroEquipo.setidLugar(0);

        Respuesta obtenida = s.modificarOtroEquipo(dtoOtroEquipo, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        // Escribimos las salidas en sus correspondientes archivos
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaModificarConOtroEquipoInexistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        int indice = 2;
        DTOOtroEquipo dtoOtroEquipo = new DTOOtroEquipo();
        dtoOtroEquipo.setDescripcion("Descripcion" + indice);
        dtoOtroEquipo.setEsPrestamo(false);
        dtoOtroEquipo.setEstado("ALTA");
        dtoOtroEquipo.setFechaAlta("2018-07-23");

        dtoOtroEquipo.setMarca("BROTHER");
        dtoOtroEquipo.setMesesGarantia(36);
        dtoOtroEquipo.setModelo("HL-5450");

        dtoOtroEquipo.setNumeroSerie("U630123456" + indice + "noexiste");
        dtoOtroEquipo.setNumeroPrecinto(indice * 111);
        dtoOtroEquipo.setPrecioDeCompra(800.0);
        dtoOtroEquipo.setTipo("SCANNER");
        dtoOtroEquipo.setUsuario("User1");
        dtoOtroEquipo.setidLugar(1);

        Respuesta obtenida = s.modificarOtroEquipo(dtoOtroEquipo, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(8, dtoOtroEquipo);

        // Escribimos las salidas en sus correspondientes archivos  
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

    @Test
    public void pruebaModificarCorrectaOtroEquipoExistente() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        String sn = FuncionalidadesComunes.generarSNrandom();
        DTOEquipo dtoOtroEquipo = FuncionalidadesComunes.crearDTOEquipoValido(sn, "SCANNER");
        dtoOtroEquipo = (DTOEquipo)s.altaOtroEquipo((DTOOtroEquipo) dtoOtroEquipo).getRetorno();
        Respuesta obtenida = s.modificarOtroEquipo((DTOOtroEquipo) dtoOtroEquipo, "user1");
        Respuesta esperada = Respuesta.nuevaRespuesta(6, dtoOtroEquipo);

        // Escribimos las salidas en sus correspondientes archivos  
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
        assertEquals(esperada.getCodigo(), obtenida.getCodigo());
    }

}
