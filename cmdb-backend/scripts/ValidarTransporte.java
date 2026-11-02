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

/** Executable-JAR HTTP checks against a new disposable SQL Server with synthetic data only. */
class ValidarTransporte {
    public static void main(String[] args) throws Exception {
        Path evidence = Path.of("docs/evidence/cmdb14/jar-http");
        Files.createDirectories(evidence);
        Path raw = Files.createTempFile("cmdb14-startup-", ".log");
        Files.setPosixFilePermissions(raw, java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"));
        var config = SqlServerTesting.configuracion().bd();
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
            var http = new TransporteHttpTesting(URI.create("http://127.0.0.1:" + port + "/cmdb-backend"));
            result.put("registration", http.registro());
            result.put("query_and_read", http.consulta());
            result.put("dto_and_write", http.escritura());
            result.put("headers_and_login", http.cabeceras());
            result.put("invalid_entities", http.entidadesInvalidas());
            result.put("cors", http.cors());
            result.put("pass", true);
            System.out.println("JAR HTTP: 24 resources, query/JSON/headers, create/update/delete and error/CORS checks OK");
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
