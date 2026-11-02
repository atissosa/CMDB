package utilidades;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

public class GeneraDocumentacionTestingTest {

    @Test
    public void recorrerDirectorioYgenerarDoc() {
        try {
            File carpeta = new File("target/legacy-test-report/");
            carpeta.mkdirs();

            File file = new File("target/legacy-test-report/informe.txt");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            Files.createDirectories(Paths.get("target/legacy-test-output"));
            try (var rutas = Files.walk(Paths.get("target/legacy-test-output"))) {
            rutas.forEach(ruta -> {
                if (Files.isRegularFile(ruta)) {
                    escribirDOC(ruta.toString());
                }
            }
            );
            }
        } catch (IOException ex) {
            Logger.getLogger(GeneraDocumentacionTestingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void escribirDOC(String archivoAcopiar) {
        String rutaDirectorio = "target/legacy-test-report/informe.txt";
        try {
            FileWriter fw1 = new FileWriter(rutaDirectorio, true);
            BufferedWriter bw1 = new BufferedWriter(fw1);
            FileReader fr2 = new FileReader(archivoAcopiar);
            BufferedReader bf1 = new BufferedReader(fr2);
            String cadenaLee = bf1.readLine();

            while (cadenaLee != null) {
                bw1.write(cadenaLee);
                bw1.newLine();
                cadenaLee = bf1.readLine();
            }

            bw1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
