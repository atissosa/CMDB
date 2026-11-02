package Equipo.PC;

import com.atissosa.cmdb_backend.dto.DTOPC;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import utilidades.EntornoTesting;
import utilidades.FuncionalidadesComunes;

public class pruebaAltaPcTest {

    public pruebaAltaPcTest() {
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
    public void pruebaAltaPcConDtoNulo() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        Respuesta obtenida = s.altaPc(null);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        // Escribimos las salidas en sus correspondientes archivos 
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaPcConTodosLosDatosNulos() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOPC dtoPc = new DTOPC();
        Respuesta obtenida = s.altaPc(dtoPc);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        // Escribimos las salidas en sus correspondientes archivos  
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaPcConTodosLosDatosVacios() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOPC dtoPc = new DTOPC();
        dtoPc.setCodigo("");
        dtoPc.setDescripcion("");
        dtoPc.setEsPrestamo(false);
        dtoPc.setEstado("");
        dtoPc.setFechaAlta("");
        dtoPc.setId(0);
        dtoPc.setIp("");
        dtoPc.setLugar("");
        dtoPc.setMarca("");
        dtoPc.setMemoriaRam(0);
        dtoPc.setMesesGarantia(0);
        dtoPc.setModelo("");
        dtoPc.setNombreHost("");
        dtoPc.setNumeroSerie("");
        dtoPc.setNumeroPrecinto(0);
        dtoPc.setPrecioDeCompra(0.0);
        dtoPc.setProcesador("");
        dtoPc.setTamanoDisco(0);
        dtoPc.setTipo("");
        dtoPc.setUsuario("");
        dtoPc.setidLugar(0);
        Respuesta obtenida = s.altaPc(dtoPc);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        // Escribimos las salidas en sus correspondientes archivos   
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaPcIpNoValida() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        DTOPC dtoPc = new DTOPC();
        dtoPc.setDescripcion("d");
        dtoPc.setEsPrestamo(false);
        dtoPc.setEstado("e");
        dtoPc.setFechaAlta("2018-07-23");
        dtoPc.setId(0);
        dtoPc.setIp("10.");
        dtoPc.setLugar("l");
        dtoPc.setMarca("m");
        dtoPc.setMemoriaRam(1);
        dtoPc.setMesesGarantia(0);
        dtoPc.setModelo("m");
        dtoPc.setNombreHost("n");
        dtoPc.setNumeroSerie("s");
        dtoPc.setNumeroPrecinto(1);
        dtoPc.setPrecioDeCompra(0.0);
        dtoPc.setProcesador("p");
        dtoPc.setTamanoDisco(500);
        dtoPc.setTipo("PC");
        dtoPc.setUsuario("user1");
        dtoPc.setidLugar(1);
        Respuesta obtenida = s.altaPc(dtoPc);
        Respuesta esperada = Respuesta.nuevaRespuesta(11, null);

        // Escribimos las salidas en sus correspondientes archivos    
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaPcCorrecta() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        
        Respuesta obtenida = s.altaPc((DTOPC)FuncionalidadesComunes.crearDTOEquipoValido(
                FuncionalidadesComunes.generarSNrandom(), "PC"));
        Respuesta esperada = Respuesta.nuevaRespuesta(5, null);

        // Escribimos las salidas en sus correspondientes archivos  
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
    }

    @Test
    public void pruebaAltaConPcExistenteEnSistema() {
        FuncionalidadesComunes.ImprimirComienzoDeTest();

        int indice = 1;
        DTOPC dtoPc = new DTOPC();
        dtoPc.setDescripcion("Descripcion" + indice);
        dtoPc.setEsPrestamo(false);
        dtoPc.setFechaAlta("2018-07-23");
        dtoPc.setIp("10.120.1." + indice);
        dtoPc.setMarca("HP");
        dtoPc.setMemoriaRam(4);
        dtoPc.setMesesGarantia(36);
        dtoPc.setModelo("Elite Desk 705 G3");
        dtoPc.setNombreHost("PC_" + indice);
        dtoPc.setNumeroSerie("MXL7242PC" + indice);
        dtoPc.setNumeroPrecinto(indice * 111);
        dtoPc.setPrecioDeCompra(800.0);
        dtoPc.setProcesador("AMD A6");
        dtoPc.setTamanoDisco(500);
        dtoPc.setTipo("PC");
        dtoPc.setUsuario("User1");
        dtoPc.setidLugar(1);
        Respuesta obtenida = s.altaPc(dtoPc);
        Respuesta esperada = Respuesta.nuevaRespuesta(7, dtoPc);

        // Escribimos las salidas en sus correspondientes archivos      
        FuncionalidadesComunes.escribirRespuesta(esperada, obtenida);
        FuncionalidadesComunes.ImprimirFinDeTest();
    }
}
