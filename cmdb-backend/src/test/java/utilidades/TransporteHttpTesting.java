package utilidades;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

/** Real HTTP checks shared by JUnit and the executable-JAR validation; no production secrets. */
public final class TransporteHttpTesting {
    private static final ObjectMapper JSON = new ObjectMapper();
    private final URI base;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    public TransporteHttpTesting(URI base) { this.base = base; }

    private record Result(int status, JsonNode body, java.net.http.HttpHeaders headers) { }
    public record Account(String username, String password) {
        String authorization() {
            return "Basic " + Base64.getEncoder().encodeToString(
                    (username + ":" + password).getBytes(StandardCharsets.UTF_8));
        }
        @Override public String toString() { return "Account[synthetic credentials omitted]"; }
    }

    /** Fresh synthetic account with the fixture admin's role, avoiding shared legacy session races. */
    public static Account account() throws Exception { return account(1); }

    public static Account account(int referenceId) throws Exception {
        var account = new Account("http14" + UUID.randomUUID().toString().replace("-", "").substring(0, 12),
                "H14!" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        var config = SqlServerTesting.configuracion().bd();
        var credentials = new Properties();
        credentials.setProperty("user", config.usuario());
        credentials.setProperty("password", config.contrasenia());
        String url = "jdbc:sqlserver://" + config.servidor() + ":" + config.puerto()
                + ";databaseName=" + config.nombre() + ";encrypt=false";
        try (var connection = DriverManager.getConnection(url, credentials);
             var statement = connection.prepareStatement(
                "INSERT dbo.Usuarios (Usuario, Contrasena, IdRol, IdDepartamento, Activo) "
                + "SELECT ?, ?, IdRol, IdDepartamento, Activo FROM dbo.Usuarios WHERE Id=?")) {
            statement.setString(1, account.username());
            statement.setString(2, account.password());
            statement.setInt(3, referenceId);
            check(statement.executeUpdate() == 1, "Synthetic transport account fixture was not prepared");
        }
        return account;
    }

    private Result call(String method, String path, String payload, String authorization, String contentType) throws Exception {
        return call(method, path, payload, authorization, contentType, Map.of());
    }

    private Result call(String method, String path, String payload, String authorization,
                        String contentType, Map<String, String> extraHeaders) throws Exception {
        var request = HttpRequest.newBuilder(base.resolve(base.getPath().replaceAll("/+$", "") + path)).timeout(Duration.ofSeconds(15));
        if (authorization != null) request.header("Authorization", authorization);
        if (contentType != null) request.header("Content-Type", contentType);
        extraHeaders.forEach(request::header);
        request.header("Accept", "application/json");
        var response = client.send(request.method(method, payload == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8)).build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        JsonNode body = JSON.nullNode();
        if (!response.body().isBlank() && response.headers().firstValue("Content-Type").orElse("").contains("application/json")) {
            body = JSON.readTree(response.body());
        }
        return new Result(response.statusCode(), body, response.headers());
    }

    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message); // Never include credentials or a whole fixture response.
    }

    private static void business(Result response, int status, int code) {
        check(response.status() == status, "Unexpected HTTP status: " + response.status() + " (expected " + status + ")");
        check(response.body().path("codigo").asInt(-1) == code,
                "Unexpected business response code: " + response.body().path("codigo").asInt(-1) + " (expected " + code + ")");
        check(response.headers().firstValue("Content-Type").orElse("").startsWith("application/json"), "JSON response provider missing");
    }

    public static JsonNode routes() throws Exception {
        try (var input = TransporteHttpTesting.class.getResourceAsStream("/http/cmdb14-routes.json")) {
            if (input == null) throw new AssertionError("Missing frozen transport route inventory");
            return JSON.readTree(input);
        }
    }

    public List<Map<String, Object>> registro() throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        for (JsonNode resource : routes()) {
            String path = resource.path("path").asText();
            var response = call("OPTIONS", path, null, null, null);
            check(response.status() == 200, "Resource not registered: " + path);
            Set<String> allow = Set.of(response.headers().firstValue("Allow").orElse("").replace(" ", "").split(","));
            for (JsonNode verb : resource.path("verbs")) check(allow.contains(verb.asText()), "Missing verb on " + path);
            result.add(Map.of("path", path, "status", response.status(), "allow", allow));
        }
        check(result.size() == 24, "Expected 24 resources");
        return result;
    }

    public Map<String, Object> consulta() throws Exception {
        Result all = call("GET", "/recursos/departamento", null, null, null);
        business(all, 200, 4);
        check(all.body().path("retorno").isArray() && !all.body().path("retorno").isEmpty(), "Read fixture missing");
        int id = all.body().path("retorno").get(0).path("id").asInt();
        Result selected = call("GET", "/recursos/departamento?id=" + id, null, null, null);
        business(selected, 200, 4);
        check(selected.body().path("retorno").size() == 1
                && selected.body().path("retorno").get(0).path("id").asInt() == id, "Query parameter was not bound");
        Result invalid = call("GET", "/recursos/departamento?id=not-an-integer", null, null, null);
        check(invalid.status() == 404, "Integer query conversion contract differs");
        return Map.of("list_status", all.status(), "selected_status", selected.status(), "query_id_bound", true,
                "invalid_integer_status", invalid.status());
    }

    public Map<String, Object> escritura() throws Exception { return escritura(false); }

    public Map<String, Object> escrituraConCabeceraFrontend() throws Exception { return escritura(true); }

    private Map<String, Object> escritura(boolean frontendHeader) throws Exception {
        Account account = account();
        String loginAuth = account.authorization();
        String auth = frontendHeader ? "Basic " + Base64.getEncoder().encodeToString(
                account.username().getBytes(StandardCharsets.UTF_8)) : loginAuth;
        int id = 0;
        boolean removed = false;
        try {
            business(call("POST", "/recursos/login", null, loginAuth, "application/json"), 200, 1);
            String name = "HTTP14-" + UUID.randomUUID();
            String description = "Descripción sintética á ñ";
            String payload = JSON.writeValueAsString(Map.of("nombre", name, "descripcion", description, "activo", true));
            Result created = call("POST", "/recursos/departamento", payload, auth, "application/json");
            business(created, 200, 5);
            id = created.body().path("retorno").path("id").asInt();
            check(id > 0, "POST did not delegate to persistent create");
            Result found = call("GET", "/recursos/departamento?id=" + id, null, null, null);
            business(found, 200, 4);
            JsonNode dto = found.body().path("retorno").get(0);
            check(dto.path("nombre").asText().equals(name) && dto.path("descripcion").asText().equals(description),
                    "JSON fields/Unicode were not persisted and serialized");
            String updated = "Actualización sintética ñ";
            String edit = JSON.writeValueAsString(Map.of("id", id, "nombre", name, "descripcion", updated, "activo", true));
            business(call("PUT", "/recursos/departamento", edit, auth, "application/json"), 200, 6);
            Result reread = call("GET", "/recursos/departamento?id=" + id, null, null, null);
            business(reread, 200, 4);
            check(reread.body().path("retorno").get(0).path("descripcion").asText().equals(updated), "PUT did not persist changes");
            business(call("DELETE", "/recursos/departamento", edit, auth, "application/json"), 200, 9);
            removed = true;
            return Map.of("post_status", 200, "post_code", 5, "put_code", 6, "delete_code", 9,
                    "persistent_readback", true, "unicode_roundtrip", true);
        } finally {
            if (id > 0 && !removed) call("DELETE", "/recursos/departamento", "{\"id\":" + id + "}", auth, "application/json");
            call("DELETE", "/recursos/login", null, auth, "application/json");
        }
    }

    public Map<String, Object> cabeceras() throws Exception {
        Account account = account();
        Result missing = call("POST", "/recursos/login", null, null, "application/json");
        business(missing, 401, 2);
        String invalid = "Basic " + Base64.getEncoder().encodeToString(
                (account.username() + ":invalid-synthetic").getBytes(StandardCharsets.UTF_8));
        business(call("POST", "/recursos/login", null, invalid, "application/json"), 401, 2);
        try {
            business(call("POST", "/recursos/login", null, account.authorization(), "application/json"), 200, 1);
            return Map.of("missing_header", 401, "invalid_credentials", 401, "valid_header", 200);
        } finally {
            check(call("DELETE", "/recursos/login", null, account.authorization(), "application/json").status() == 204,
                    "Void logout response contract differs");
        }
    }

    public Map<String, Object> entidadesInvalidas() throws Exception {
        int malformed = call("POST", "/recursos/departamento", "{", null, "application/json").status();
        int unsupported = call("POST", "/recursos/departamento", "synthetic", null, "text/plain").status();
        check(malformed == 400, "Malformed JSON must fail at transport binding");
        check(unsupported == 415, "Unsupported media type must fail before business delegation");
        return Map.of("malformed_json", malformed, "unsupported_media_type", unsupported);
    }

    public Map<String, Object> cors() throws Exception {
        String allowedOrigin = "http://localhost:8080";
        String deniedOrigin = "http://no-autorizado.invalid:9090";

        Map<String, String> preflight = new LinkedHashMap<>();
        preflight.put("Origin", allowedOrigin);
        preflight.put("Access-Control-Request-Method", "POST");
        preflight.put("Access-Control-Request-Headers", "authorization,content-type");
        var allowed = call("OPTIONS", "/recursos/departamento", null, null, null, preflight);
        check(allowed.status() == 200, "CORS preflight allowed origin failed");
        check(allowed.headers().firstValue("Access-Control-Allow-Origin").orElse("").equals(allowedOrigin),
            "CORS allowed origin was not echoed explicitly");
        check(allowed.headers().firstValue("Access-Control-Allow-Credentials").isEmpty(),
            "CORS credentials should not be enabled when not configured");
        check(allowed.headers().firstValue("Access-Control-Allow-Methods").orElse("").contains("POST"),
            "CORS methods do not include POST");
        String allowHeaders = allowed.headers().firstValue("Access-Control-Allow-Headers").orElse("").toLowerCase();
        check(allowHeaders.contains("authorization") && allowHeaders.contains("content-type"),
            "CORS headers do not include authorization and content-type");
        String vary = allowed.headers().firstValue("Vary").orElse("");
        check(vary.contains("Origin") && vary.contains("Access-Control-Request-Method")
                && vary.contains("Access-Control-Request-Headers"),
            "CORS preflight Vary headers are incomplete");

        Map<String, String> deniedPreflight = new LinkedHashMap<>(preflight);
        deniedPreflight.put("Origin", deniedOrigin);
        var denied = call("OPTIONS", "/recursos/departamento", null, null, null, deniedPreflight);
        check(denied.status() == 200, "CORS denied preflight should keep transport status");
        check(denied.headers().firstValue("Access-Control-Allow-Origin").isEmpty(),
            "CORS denied origin unexpectedly received allow-origin");

        var noOrigin = call("OPTIONS", "/recursos/departamento", null, null, null,
            Map.of("Access-Control-Request-Method", "POST", "Access-Control-Request-Headers", "authorization"));
        check(noOrigin.headers().firstValue("Access-Control-Allow-Origin").isEmpty(),
            "CORS without Origin should not emit CORS allow headers");

        var malformed = call("POST", "/recursos/departamento", "{", null, "application/json", Map.of("Origin", allowedOrigin));
        check(malformed.status() == 400, "Malformed JSON transport contract differs");
        check(malformed.headers().firstValue("Access-Control-Allow-Origin").orElse("").equals(allowedOrigin),
            "CORS must be applied to error responses for allowed origins");

        return Map.of(
            "allowed_origin", allowedOrigin,
            "denied_origin_without_allow_header", true,
            "preflight_authorization_supported", true,
            "error_response_has_cors", true,
            "vary_origin_present", true);
    }
}
