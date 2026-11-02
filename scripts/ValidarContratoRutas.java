import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import utilidades.SqlServerTesting;
import utilidades.TransporteHttpTesting;
import utilidades.ContratoRutasHttpTesting;
import com.atissosa.cmdb_backend.dto.Respuesta;

/** Executable-JAR HTTP checks against a new disposable SQL Server with synthetic data only. */
class ValidarContratoRutas {
    public static void main(String[] args) throws Exception {
        Path evidence = Path.of("docs/evidence/cmdb15/jar-http");
        Files.createDirectories(evidence);
        Path raw = Files.createTempFile("cmdb15-startup-", ".log");
        Files.setPosixFilePermissions(raw, java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"));
        var config = SqlServerTesting.configuracion().bd();
        SqlServerTesting.configurar();
        Respuesta.cargarMensajes();
        var command = List.of("java", "-jar", "target/cmdb-backend-0.0.1-SNAPSHOT.jar",
                "--spring.profiles.active=test", "--server.address=127.0.0.1", "--server.port=0");
        var builder = new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(raw.toFile());
        builder.environment().keySet().removeIf(k -> k.startsWith("CMDB_") || k.startsWith("SPRING_")
                || List.of("JAVA_TOOL_OPTIONS", "JDK_JAVA_OPTIONS", "_JAVA_OPTIONS").contains(k));
        builder.environment().put("CMDB_BD_SERVIDOR", config.servidor());
        builder.environment().put("CMDB_BD_PUERTO", config.puerto().toString());
        builder.environment().put("CMDB_BD_NOMBRE", config.nombre());
        builder.environment().put("CMDB_DB_USERNAME", config.usuario());
        builder.environment().put("CMDB_DB_PASSWORD", config.contrasenia());
        var result = new LinkedHashMap<String, Object>();
        result.put("command", command);
        result.put("profile", "test");
        result.put("credential_source", "generated container credentials via child environment; values omitted");
        result.put("pass", false);
        var process = builder.start();
        try {
            int port = 0;
            long deadline = System.nanoTime() + Duration.ofSeconds(45).toNanos();
            while (System.nanoTime() < deadline) {
                String log = Files.readString(raw);
                var matcher = Pattern.compile("Tomcat started on port (\\d+)").matcher(log);
                if (matcher.find() && log.contains("Started CmdbBackendApplication")) {
                    port = Integer.parseInt(matcher.group(1));
                    break;
                }
                if (!process.isAlive()) throw new AssertionError("JAR exited during startup");
                Thread.sleep(100);
            }
            if (port == 0) throw new AssertionError("JAR startup timed out");
            URI origin = URI.create("http://127.0.0.1:" + port);
            var contract = new ContratoRutasHttpTesting(origin);
            result.put("all_operations", contract.todasLasOperaciones());
            result.put("frontend_calls", contract.llamadasFrontend());
            result.put("query_parameters", contract.parametros());
            result.put("invalid_routes_methods", contract.rutasInvalidas());
            var http = new TransporteHttpTesting(URI.create(origin + ContratoRutasHttpTesting.matrix().path("servlet_context").asText()));
            result.put("frontend_header_and_delete_body", http.escrituraConCabeceraFrontend());
            result.put("pass", true);
            System.out.println("JAR contract: all 51 operations, 57 frontend expansions, parameter and invalid-route cases, DELETE body and frontend header OK");
        } finally {
            process.destroy();
            if (!process.waitFor(10, TimeUnit.SECONDS)) { process.destroyForcibly(); process.waitFor(); }
            String log = Files.readString(raw);
            boolean leaked = log.contains(config.contrasenia());
            Files.writeString(evidence.resolve("startup.log"), log.replace(config.contrasenia(), "[REDACTED]"));
            Files.deleteIfExists(raw);
            result.put("application_stopped", !process.isAlive());
            result.put("database_password_leaked", leaked);
            if (leaked) result.put("pass", false);
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(evidence.resolve("results.json").toFile(), result);
            if (leaked) throw new AssertionError("Generated credential found in output; redacted evidence retained");
        }
        // SqlServerTesting shutdown hook and Ryuk remove the database when this JVM exits.
    }
}
