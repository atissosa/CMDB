package utilidades;

import com.atissosa.cmdb_backend.dto.DTODepartamentoCentral;
import com.atissosa.cmdb_backend.dto.DTOEquipo;
import com.atissosa.cmdb_backend.dto.DTOImpresora;
import com.atissosa.cmdb_backend.dto.DTOLugar;
import com.atissosa.cmdb_backend.dto.DTOOtroEquipo;
import com.atissosa.cmdb_backend.dto.DTOPC;
import com.atissosa.cmdb_backend.dto.DTOService;
import com.atissosa.cmdb_backend.dto.DTOValidadora;
import com.atissosa.cmdb_backend.dto.Respuesta;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.UUID;
import com.atissosa.cmdb_backend.logic.Sistema;

public class FuncionalidadesComunes {

    private static String rutaDirectorio;

    public static void crearArchivosYCarpetaDeClase() {
        String ruta = "target/legacy-test-output/" + new Exception().getStackTrace()[1].getClassName();
        rutaDirectorio = ruta;
        File carpeta = new File(ruta);
        carpeta.mkdirs();
        crearArchivos();
    }

    private static void crearArchivos() {
        try {
            String rutaEsperada = rutaDirectorio + "/" + "_" + "esperada" + ".txt";
            String rutaObtenida = rutaDirectorio + "/" + "_" + "obtenida" + ".txt";
            File fileEsperada = new File(rutaEsperada);
            File fileObtenida = new File(rutaObtenida);
//            Si el archivo no existe es creado
            if (fileEsperada.exists()) {
                fileEsperada.delete();
            }
            if (fileObtenida.exists()) {
                fileObtenida.delete();
            }
            fileObtenida.createNewFile();
            fileEsperada.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ImprimirComienzoDeTest() {
        String rutaEsperada = rutaDirectorio + "/" + "_" + "esperada" + ".txt";
        String rutaObtenida = rutaDirectorio + "/" + "_" + "obtenida" + ".txt";
        try {
            FileWriter fw1 = new FileWriter(rutaEsperada, true);
            BufferedWriter bw1 = new BufferedWriter(fw1);
            bw1.write("****************** COMIENZO *********************");
            bw1.newLine();
            //Retorna el nombre del metodo desde el cual se hace el llamado
            bw1.write("         ");
            bw1.write(new Exception().getStackTrace()[1].getMethodName() + "()");
            bw1.newLine();
            bw1.write("****************************************************");
            bw1.newLine();

            bw1.close();

            FileWriter fw2 = new FileWriter(rutaObtenida, true);
            BufferedWriter bw2 = new BufferedWriter(fw2);
            bw2.write("****************** COMIENZO *********************");
            bw2.newLine();
            //Retorna el nombre del metodo desde el cual se hace el llamado
            bw2.write("         ");
            bw2.write(new Exception().getStackTrace()[1].getMethodName() + "()");
            bw2.newLine();
            bw2.write("****************************************************");
            bw2.newLine();

            bw2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ImprimirFinDeTest() {
        String rutaEsperada = rutaDirectorio + "/" + "_" + "esperada" + ".txt";
        String rutaObtenida = rutaDirectorio + "/" + "_" + "obtenida" + ".txt";
        try {
            FileWriter fw = new FileWriter(rutaEsperada, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("***************************************************");
            bw.newLine();
            //Retorna el nombre del metodo desde el cual se hace el llamado
            bw.write("         ");
            bw.write(new Exception().getStackTrace()[1].getMethodName() + "()");
            bw.newLine();
            bw.write("------------------------ FIN ------------------------");
            bw.newLine();

            bw.close();

            FileWriter fw2 = new FileWriter(rutaObtenida, true);
            BufferedWriter bw2 = new BufferedWriter(fw2);
            bw2.write("***************************************************");
            bw2.newLine();
            //Retorna el nombre del metodo desde el cual se hace el llamado
            bw2.write("         ");
            bw2.write(new Exception().getStackTrace()[1].getMethodName() + "()");
            bw2.newLine();
            bw2.write("------------------------ FIN ------------------------");
            bw2.newLine();

            bw2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void escribirRespuesta(Respuesta respuestaEsperada, Respuesta respuestaObtenida) {
        String rutaEsperada = rutaDirectorio + "/" + "_" + "esperada" + ".txt";
        String rutaObtenida = rutaDirectorio + "/" + "_" + "obtenida" + ".txt";
        try {
            FileWriter fw = new FileWriter(rutaEsperada, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(respuestaEsperada.getCodigo() + " - " + respuestaEsperada.getMensaje());
            bw.newLine();
            bw.close();

            FileWriter fw2 = new FileWriter(rutaObtenida, true);
            BufferedWriter bw2 = new BufferedWriter(fw2);
            bw2.write(respuestaObtenida.getCodigo() + " - " + respuestaObtenida.getMensaje());
            bw2.newLine();
            bw2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean archivosIguales() {
        boolean iguales = true;
        String rutaEsperada = rutaDirectorio + "/" + "_" + "esperada" + ".txt";
        String rutaObtenida = rutaDirectorio + "/" + "_" + "obtenida" + ".txt";
        try {
            FileReader fr1 = new FileReader(rutaEsperada);
            FileReader fr2 = new FileReader(rutaObtenida);

            BufferedReader bf1 = new BufferedReader(fr1);
            BufferedReader bf2 = new BufferedReader(fr2);
            String sCadena1 = bf1.readLine();
            String sCadena2 = bf2.readLine();

            while ((sCadena1 != null) && (sCadena2 != null) && iguales) {
                if (!sCadena1.equals(sCadena2)) {
                    iguales = false;
                }

                sCadena1 = bf1.readLine();
                sCadena2 = bf2.readLine();
            }
            iguales = ((iguales) && (sCadena1 == null) && (sCadena2 == null));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return iguales;
    }

    public static String generarSNrandom() {
        // Keep serial values short and unique to avoid schema-length flakes.
        return "TEST" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public static DTOEquipo crearDTOEquipoValido(String SN, String tipo) {
        DTOEquipo dto = null;
        int numeroPrecinto = Math.abs(SN.hashCode());
        if (numeroPrecinto == 0) {
            numeroPrecinto = 1;
        }
        switch (tipo) {
            case "PC":
                DTOPC dtoPC = new DTOPC();
                dtoPC.setCodigo(SN);
                dtoPC.setNumeroSerie(SN);
                dtoPC.setMarca(SN);
                dtoPC.setModelo(SN);
                dtoPC.setPrecioDeCompra(100.0);
                dtoPC.setFechaAlta("2018-09-24");
                dtoPC.setMesesGarantia(12);
                dtoPC.setEsPrestamo(false);
                dtoPC.setNumeroPrecinto(numeroPrecinto);
                dtoPC.setidLugar(1);
                dtoPC.setidLugarActual(150);
                dtoPC.setEstado("ALTA");
                dtoPC.setUsuario("user1");
                dtoPC.setIp("0.0.0.0");
                dtoPC.setNombreHost(SN);
                dtoPC.setProcesador(SN);
                dtoPC.setMemoriaRam(4000);
                dtoPC.setTamanoDisco(500);
                dtoPC.setTipo(tipo);
                dto = dtoPC;
                break;
            case "IMPRESORA":
                DTOImpresora dtoImpresora = new DTOImpresora();
                dtoImpresora.setCodigo(SN);
                dtoImpresora.setNumeroSerie(SN);
                dtoImpresora.setMarca(SN);
                dtoImpresora.setModelo(SN);
                dtoImpresora.setPrecioDeCompra(100.0);
                dtoImpresora.setFechaAlta("2018-09-24");
                dtoImpresora.setMesesGarantia(12);
                dtoImpresora.setEsPrestamo(false);
                dtoImpresora.setNumeroPrecinto(numeroPrecinto);
                dtoImpresora.setidLugar(1);
                dtoImpresora.setidLugarActual(150);
                dtoImpresora.setEstado("ALTA");
                dtoImpresora.setUsuario("user1");
                dtoImpresora.setIp("0.0.0.0");
                dtoImpresora.setNombreHost(SN);
                dtoImpresora.setOpcion("LASER_MONOCROMO");
                dtoImpresora.setMultifuncion(false);
                dtoImpresora.setConexion("RED");
                dtoImpresora.setTipo(tipo);
                dto = dtoImpresora;
                break;
            case "VALIDADORA":
                DTOValidadora dtoValidadora = new DTOValidadora();
                dtoValidadora.setCodigo(SN);
                dtoValidadora.setNumeroSerie(SN);
                dtoValidadora.setMarca(SN);
                dtoValidadora.setModelo(SN);
                dtoValidadora.setPrecioDeCompra(100.0);
                dtoValidadora.setFechaAlta("2018-09-24");
                dtoValidadora.setMesesGarantia(12);
                dtoValidadora.setEsPrestamo(false);
                dtoValidadora.setNumeroPrecinto(numeroPrecinto);
                dtoValidadora.setidLugar(1);
                dtoValidadora.setidLugarActual(150);
                dtoValidadora.setEstado("ALTA");
                dtoValidadora.setUsuario("user1");
                dtoValidadora.setTipo(tipo);
                dto = dtoValidadora;
                break;
            default:
                DTOOtroEquipo dtoOtroEquipo = new DTOOtroEquipo();
                dtoOtroEquipo.setCodigo(SN);
                dtoOtroEquipo.setNumeroSerie(SN);
                dtoOtroEquipo.setMarca(SN);
                dtoOtroEquipo.setModelo(SN);
                dtoOtroEquipo.setPrecioDeCompra(100.0);
                dtoOtroEquipo.setFechaAlta("2018-09-24");
                dtoOtroEquipo.setMesesGarantia(12);
                dtoOtroEquipo.setEsPrestamo(false);
                dtoOtroEquipo.setNumeroPrecinto(numeroPrecinto);
                dtoOtroEquipo.setidLugar(1);
                dtoOtroEquipo.setidLugarActual(150);
                dtoOtroEquipo.setEstado("ALTA");
                dtoOtroEquipo.setUsuario("user1");
                dtoOtroEquipo.setTipo(tipo);
                dto = dtoOtroEquipo;
                break;
        }
        return dto;
    }

    public static DTOEquipo crearEquipoConTodosLosValoresNulos(String tipo) {
        DTOEquipo dto = null;
        switch (tipo) {
            case "PC":
                DTOPC dtoPC = new DTOPC();
                dto = dtoPC;
                break;
            case "IMPRESORA":
                DTOImpresora dtoImpresora = new DTOImpresora();
                dto = dtoImpresora;
                break;
            case "VALIDADORA":
                DTOValidadora dtoValidadora = new DTOValidadora();
                dto = dtoValidadora;
                break;
            default:
                DTOOtroEquipo dtoOtroEquipo = new DTOOtroEquipo();
                dto = dtoOtroEquipo;
                break;
        }
        return dto;
    }

    public static DTOEquipo crearEquipoConValoresVacios(String tipo) {
        DTOEquipo dto = null;
        switch (tipo) {
            case "PC":
                DTOPC dtoPC = new DTOPC();
                dtoPC.setCodigo("");
                dtoPC.setNumeroSerie("");
                dtoPC.setMarca("");
                dtoPC.setModelo("");
                dtoPC.setPrecioDeCompra(0.0);
                dtoPC.setFechaAlta("");
                dtoPC.setMesesGarantia(0);
                dtoPC.setEsPrestamo(false);
                dtoPC.setNumeroPrecinto(0);
                dtoPC.setidLugar(0);
                dtoPC.setUsuario("");
                dtoPC.setIp("");
                dtoPC.setNombreHost("");
                dtoPC.setProcesador("");
                dtoPC.setMemoriaRam(0);
                dtoPC.setTamanoDisco(0);
                dto = dtoPC;
                break;
            case "IMPRESORA":
                DTOImpresora dtoImpresora = new DTOImpresora();
                dtoImpresora.setCodigo("");
                dtoImpresora.setNumeroSerie("");
                dtoImpresora.setMarca("");
                dtoImpresora.setModelo("");
                dtoImpresora.setPrecioDeCompra(0.0);
                dtoImpresora.setFechaAlta("");
                dtoImpresora.setMesesGarantia(0);
                dtoImpresora.setEsPrestamo(false);
                dtoImpresora.setNumeroPrecinto(0);
                dtoImpresora.setidLugar(0);
                dtoImpresora.setUsuario("");
                dtoImpresora.setIp("");
                dtoImpresora.setNombreHost("");
                dtoImpresora.setOpcion("");
                dtoImpresora.setMultifuncion(false);
                dtoImpresora.setConexion("");
                dto = dtoImpresora;
                break;
            case "VALIDADORA":
                DTOValidadora dtoValidadora = new DTOValidadora();
                dtoValidadora.setCodigo("");
                dtoValidadora.setNumeroSerie("");
                dtoValidadora.setMarca("");
                dtoValidadora.setModelo("");
                dtoValidadora.setPrecioDeCompra(0.0);
                dtoValidadora.setFechaAlta("");
                dtoValidadora.setMesesGarantia(0);
                dtoValidadora.setEsPrestamo(false);
                dtoValidadora.setNumeroPrecinto(0);
                dtoValidadora.setidLugar(0);
                dtoValidadora.setUsuario("");
                dto = dtoValidadora;
                break;
            default:
                DTOOtroEquipo dtoOtroEquipo = new DTOOtroEquipo();
                dtoOtroEquipo.setCodigo("");
                dtoOtroEquipo.setNumeroSerie("");
                dtoOtroEquipo.setMarca("");
                dtoOtroEquipo.setModelo("");
                dtoOtroEquipo.setPrecioDeCompra(0.0);
                dtoOtroEquipo.setFechaAlta("");
                dtoOtroEquipo.setMesesGarantia(0);
                dtoOtroEquipo.setEsPrestamo(false);
                dtoOtroEquipo.setNumeroPrecinto(0);
                dtoOtroEquipo.setidLugar(0);
                dtoOtroEquipo.setUsuario("");
                dto = dtoOtroEquipo;
                break;
        }
        return dto;
    }

    public static DTOLugar crearLugarValido(String nombre, String tipo) {
        DTOLugar lugar = null;
        switch (tipo) {
            case "SERVICE":
                DTOService service = new DTOService();
                service.setActivo(true);
                service.setDescripcion(nombre);
                service.setDireccion(nombre);
                service.setNombre(nombre);
                lugar = service;
                break;
            case "DEPARTAMENTO":
                DTODepartamentoCentral dep = new DTODepartamentoCentral();
                dep.setActivo(true);
                dep.setDescripcion(nombre);
                dep.setNombre(nombre);
                lugar = dep;
                break;
        }
        return lugar;
    }

    public static void iniciarSessionEnSistema(Sistema s) {
        s.logout("user1");
        s.login("user1", "pass.1234");
    }
}
