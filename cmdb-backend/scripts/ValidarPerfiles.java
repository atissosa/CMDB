import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import utilidades.SqlServerTesting;

/** Standalone integration matrix: requires the compiled test classpath and Docker. */
class ValidarPerfiles {
    static final ObjectMapper JSON = new ObjectMapper();
    static final Path EVIDENCE = Path.of("docs/evidence/cmdb12/matrix");
    static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    static final List<Map<String, Object>> RESULTS = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Files.createDirectories(EVIDENCE);
        var config = SqlServerTesting.configuracion(); // New disposable database, never an external one.
        try {
            for (String profile : List.of("dev", "test", "prod")) {
                run(profile, profile, null, profile, config);
            }
            run("sin-perfil", "", "Seleccione exactamente un perfil", "", config);
            run("dos-perfiles", "dev,test", "Seleccione exactamente un perfil", "", config);
            run("falta-servidor", "test", "Configuración obligatoria ausente: cmdb.bd.servidor", "", config);
            run("proveedor-invalido", "test", "Configuración inválida en cmdb.bd.proveedor", "", config);
            run("login-invalido", "test", "Configuración inválida en cmdb.login.tipo", "", config);
            run("puerto-invalido", "test", "cmdb.bd.puerto debe estar entre 1 y 65535", "", config);
        } finally {
            JSON.writerWithDefaultPrettyPrinter().writeValue(EVIDENCE.resolve("results.json").toFile(), RESULTS);
        }
        System.out.println("Matriz completa OK: " + RESULTS.size() + " casos; credenciales no registradas");
        // SqlServerTesting's shutdown hook and Ryuk remove the disposable container.
    }

    static void run(String name, String profile, String expectedError, String override,
                    com.atissosa.cmdb_backend.config.ConfiguracionBackend config) throws Exception {
        Path external = Files.createTempFile("cmdb12-config-", ".properties");
        Path raw = Files.createTempFile("cmdb12-output-", ".log");
        Files.setPosixFilePermissions(external, PosixFilePermissions.fromString("rw-------"));
        Files.setPosixFilePermissions(raw, PosixFilePermissions.fromString("rw-------"));
        String properties = "cmdb.bd.nombre=" + config.bd().nombre() + "\n"
                + "cmdb.bd.puerto=" + config.bd().puerto() + "\n"
                + "cmdb.departamento.defecto=DesdeArchivo\n";
        if (!name.equals("falta-servidor")) properties += "cmdb.bd.servidor=" + config.bd().servidor() + "\n";
        Files.writeString(external, properties); // Non-sensitive config only; credentials go via environment.
        List<String> command = new ArrayList<>(List.of("java"));
        if (override.equals("dev") || override.equals("prod")) {
            command.add("-Dcmdb.departamento.defecto=Nombre del depatamento");
        }
        if (profile.equals("prod")) command.add("-Dcmdb.bd.trust-server-certificate=true");
        command.addAll(List.of("-jar", "target/cmdb-backend-0.0.1-SNAPSHOT.jar",
                "--server.address=127.0.0.1", "--server.port=0",
                "--spring.config.additional-location=" + external.toUri()));
        if (override.equals("prod")) {
            command.add("--spring.profiles.active=prod");
            command.add("--cmdb.departamento.defecto=Mesa de Ayuda");
        }
        if (name.equals("proveedor-invalido")) command.add("--cmdb.bd.proveedor=INVALIDO");
        if (name.equals("login-invalido")) command.add("--cmdb.login.tipo=INVALIDO");
        if (name.equals("puerto-invalido")) command.add("--cmdb.bd.puerto=70000");
        ProcessBuilder builder = new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(raw.toFile());
        var environment = builder.environment();
        environment.keySet().removeIf(k -> k.startsWith("CMDB_") || k.startsWith("SPRING_")
                || List.of("JAVA_TOOL_OPTIONS", "JDK_JAVA_OPTIONS", "_JAVA_OPTIONS").contains(k));
        if (!profile.isEmpty()) environment.put("SPRING_PROFILES_ACTIVE", profile);
        environment.put("CMDB_DB_USERNAME", config.bd().usuario());
        environment.put("CMDB_DB_PASSWORD", config.bd().contrasenia());
        environment.put("CMDB_DEPARTAMENTO_DEFECTO", "Mesa de Ayuda");
        var result = new LinkedHashMap<String, Object>();
        result.put("case", name);
        result.put("command", command);
        result.put("credential_source", "generated disposable SQL Server credentials via child environment");
        result.put("environment_profile", profile);
        result.put("pass", false);
        RESULTS.add(result);
        Process process = builder.start();
        try {
            long deadline = System.nanoTime() + Duration.ofSeconds(45).toNanos();
            String log = "";
            int port = 0;
            while (System.nanoTime() < deadline) {
                log = Files.readString(raw);
                var matcher = Pattern.compile("Tomcat started on port (\\d+)").matcher(log);
                if (matcher.find() && log.contains("Started CmdbBackendApplication")) {
                    port = Integer.parseInt(matcher.group(1));
                    break;
                }
                if (!process.isAlive()) break;
                Thread.sleep(100);
            }
            if (expectedError != null) {
                if (process.isAlive() || process.exitValue() == 0 || !log.contains(expectedError)
                        || log.contains("legacyReady=true") || log.contains("Started CmdbBackendApplication")) {
                    throw new AssertionError("Negative profile check failed: " + name);
                }
                result.put("exit", process.exitValue());
                result.put("expected_error", expectedError);
                result.put("legacy_initialized", false);
            } else {
                String department = override.equals("dev") ? "Nombre del depatamento" : "Mesa de Ayuda";
                String expected = "ambiente=" + profile + " proveedor=MSSQLSERVER servidor="
                        + config.bd().servidor() + " puerto=" + config.bd().puerto() + " base="
                        + config.bd().nombre() + " login=CLASICO departamento=" + department
                        + " encrypt=" + profile.equals("prod") + " trustServerCertificate="
                        + profile.equals("prod") + " legacyReady=true";
                if (port == 0 || !log.contains(expected)) throw new AssertionError("Resolved configuration differs: " + name);
                HttpResponse<String> options = send(port, "/cmdb-backend/recursos/login", "OPTIONS");
                if (options.statusCode() != 200) throw new AssertionError("OPTIONS failed");
                HttpResponse<String> response = send(port, "/cmdb-backend/recursos/departamento", "GET");
                var body = JSON.readTree(response.body());
                if (response.statusCode() != 200 || body.path("codigo").asInt() != 4
                        || !body.path("retorno").isArray() || body.path("retorno").isEmpty()) {
                    throw new AssertionError("Legacy read from isolated DB failed: " + name);
                }
                String afterRequest = Files.readString(raw);
                if (afterRequest.contains("No se pudo establecer la conexión")
                        || afterRequest.contains("NoClassDefFoundError")) throw new AssertionError("Legacy initialization failed");
                result.put("resolved_non_sensitive", expected);
                result.put("options_status", options.statusCode());
                result.put("legacy_read_status", response.statusCode());
                result.put("legacy_read_code", body.path("codigo").asInt());
                result.put("legacy_read_count", body.path("retorno").size());
                result.put("override_verified", switch (override) {
                    case "dev" -> "system property overrides environment and additional file";
                    case "test" -> "environment overrides additional file";
                    default -> "command line overrides system property, environment and additional file";
                });
            }
            result.put("pass", true);
            System.out.println(name + ": OK");
        } finally {
            process.destroy();
            if (!process.waitFor(10, TimeUnit.SECONDS)) { process.destroyForcibly(); process.waitFor(); }
            String safeLog = Files.readString(raw).replace(config.bd().contrasenia(), "[REDACTED]");
            Files.writeString(EVIDENCE.resolve(name + ".log"), safeLog);
            Files.deleteIfExists(raw);
            Files.deleteIfExists(external);
            result.put("application_stopped", !process.isAlive());
        }
    }

    static HttpResponse<String> send(int port, String path, String method) throws Exception {
        return HTTP.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(15)).method(method, HttpRequest.BodyPublishers.noBody()).build(),
                HttpResponse.BodyHandlers.ofString());
    }
}
