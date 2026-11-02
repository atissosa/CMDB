package utilidades;

import com.atissosa.cmdb_backend.dto.Respuesta;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Contract checks use untouched controller-to-Sistema delegation as the non-HTTP reference. */
public final class ContratoRutasHttpTesting {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String AUTH = "Basic " + Base64.getEncoder().encodeToString(
            "cmdb15-nonexistent:synthetic".getBytes(StandardCharsets.UTF_8));
    private final URI origin;
    private final JsonNode matrix;
    private final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(5)).build();

    public ContratoRutasHttpTesting(URI origin) throws Exception {
        this.origin = origin;
        this.matrix = matrix();
    }

    public static JsonNode matrix() throws Exception {
        try (var input = ContratoRutasHttpTesting.class.getResourceAsStream("/http/cmdb15-contract.json")) {
            if (input == null) throw new AssertionError("Missing source-derived route contract");
            return JSON.readTree(input);
        }
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private record Expected(int status, Integer code) { }

    /** Compare status/business code only. Payload semantics are intentionally left to CMDB-16. */
    private Expected reference(JsonNode operation, Map<String, String> query) throws Exception {
        Class<?> resource = Class.forName("com.atissosa.cmdb_backend.controllers." + operation.path("class").asText());
        String resourcePath = "/recursos/" + resource.getAnnotation(jakarta.ws.rs.Path.class).value();
        check(resourcePath.equals(operation.path("resource_path").asText()), "Resource path drift");
        var method = java.util.Arrays.stream(resource.getDeclaredMethods())
                .filter(m -> m.getName().equals(operation.path("method").asText())).findFirst().orElseThrow();
        boolean matchingVerb = java.util.Arrays.stream(method.getAnnotations()).anyMatch(a -> {
            var http = a.annotationType().getAnnotation(HttpMethod.class);
            return http != null && http.value().equals(operation.path("verb").asText());
        });
        check(matchingVerb, "HTTP verb annotation drift");
        var headers = (HttpHeaders) Proxy.newProxyInstance(HttpHeaders.class.getClassLoader(), new Class<?>[]{HttpHeaders.class},
                (proxy, called, args) -> {
                    if (called.getName().equals("getHeaderString")) return "Authorization".equalsIgnoreCase((String) args[0]) ? AUTH : null;
                    if (called.getName().equals("getRequestHeaders")) {
                        var values = new MultivaluedHashMap<String, String>();
                        values.add("Authorization", AUTH);
                        return values;
                    }
                    throw new UnsupportedOperationException("Unexpected header reference method");
                });
        Object[] args = new Object[method.getParameterCount()];
        for (int i = 0; i < args.length; i++) {
            var parameter = method.getParameters()[i];
            QueryParam key = parameter.getAnnotation(QueryParam.class);
            if (key != null) {
                String value = query.get(key.value());
                // HTTP decoders produce strings; do not pass an interned literal "" to legacy code
                // that still uses identity comparisons. This preserves, rather than fixes, that behavior.
                args[i] = parameter.getType().equals(Integer.class)
                        ? value == null || value.isEmpty() ? null : Integer.valueOf(value) : value == null ? null : new String(value);
            } else if (parameter.isAnnotationPresent(Context.class)) args[i] = headers;
            else args[i] = parameter.getType().getConstructor().newInstance(); // The HTTP body is the equivalent {}.
        }
        Object result = method.invoke(resource.getConstructor().newInstance(), args);
        if (method.getReturnType().equals(void.class)) return new Expected(204, null);
        try (Response response = (Response) result) {
            check(response.getEntity() instanceof Respuesta, "Reference does not delegate to a business response");
            return new Expected(response.getStatus(), ((Respuesta) response.getEntity()).getCodigo());
        }
    }

    private Map<String, Object> execute(JsonNode operation, Map<String, String> query, String label) throws Exception {
        Expected expected = reference(operation, query);
        String path = operation.path("migrated_full_path").asText();
        String suffix = query.isEmpty() ? "" : "?" + query.entrySet().stream().map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(java.util.stream.Collectors.joining("&"));
        String verb = operation.path("verb").asText();
        var method = Class.forName("com.atissosa.cmdb_backend.controllers." + operation.path("class").asText())
                .getDeclaredMethods();
        var signature = java.util.Arrays.stream(method).filter(m -> m.getName().equals(operation.path("method").asText())).findFirst().orElseThrow();
        boolean body = java.util.Arrays.stream(signature.getParameters()).anyMatch(p -> !p.isAnnotationPresent(QueryParam.class) && !p.isAnnotationPresent(Context.class));
        URI uri = origin.resolve(path + suffix);
        var request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(20)).header("Accept", "application/json")
                .header("Content-Type", "application/json").header("Authorization", AUTH)
                .method(verb, body ? HttpRequest.BodyPublishers.ofString("{}") : HttpRequest.BodyPublishers.noBody()).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Integer code = expected.code() == null ? null : JSON.readTree(response.body()).path("codigo").asInt(-1);
        check(response.statusCode() != 404 && response.statusCode() != 405, "Mapping failure on " + verb + " " + path);
        check(response.statusCode() == expected.status(), "HTTP status differs from direct legacy delegation on " + verb + " " + path + " [" + label + "]: observed " + response.statusCode() + ", reference " + expected.status());
        check(java.util.Objects.equals(code, expected.code()), "Business error differs on mapped operation " + verb + " " + path);
        check(response.headers().allValues("Location").isEmpty() && response.statusCode() / 100 != 3, "Unexpected redirect");
        var result = new LinkedHashMap<String, Object>();
        result.put("case", label);
        result.put("verb", verb);
        result.put("full_path", path + suffix);
        result.put("status", response.statusCode());
        result.put("business_code", code);
        result.put("reference_status", expected.status());
        result.put("reference_code", expected.code());
        result.put("json_body_sent", body);
        result.put("redirect", false);
        result.put("classification", response.statusCode() >= 400 ? "expected business response; mapping matched" : "mapped success");
        return result;
    }

    private static String encode(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }

    public List<Map<String, Object>> todasLasOperaciones() throws Exception {
        var results = new ArrayList<Map<String, Object>>();
        for (JsonNode operation : matrix.path("operations")) results.add(execute(operation, Map.of(), "matrix"));
        check(results.size() == 51, "Expected all 51 public operations");
        return results;
    }

    public List<Map<String, Object>> llamadasFrontend() throws Exception {
        var results = new ArrayList<Map<String, Object>>();
        for (JsonNode call : matrix.path("frontend_calls")) {
            JsonNode operation = null;
            for (JsonNode candidate : matrix.path("operations")) {
                if (candidate.path("legacy_full_path").asText().equals(call.path("legacy_full_path").asText())
                        && candidate.path("verb").asText().equals(call.path("verb").asText())) operation = candidate;
            }
            check(operation != null, "Frontend request missing in public matrix");
            var query = new LinkedHashMap<String, String>();
            for (JsonNode key : call.path("query_keys")) {
                String name = key.asText();
                query.put(name, name.equals("id") || name.equals("numero") ? "-150000" : "CMDB15 á & +");
            }
            results.add(execute(operation, query, "frontend-line-" + call.path("line").asInt()));
        }
        check(results.size() == matrix.path("frontend_expanded_calls").asInt(), "All frontend expansions must be exercised");
        return results;
    }

    public List<Map<String, Object>> parametros() throws Exception {
        var results = new ArrayList<Map<String, Object>>();
        for (JsonNode operation : matrix.path("operations")) {
            if (!operation.path("verb").asText().equals("GET")) continue;
            var resource = Class.forName("com.atissosa.cmdb_backend.controllers." + operation.path("class").asText());
            var method = java.util.Arrays.stream(resource.getDeclaredMethods()).filter(m -> m.getName().equals(operation.path("method").asText())).findFirst().orElseThrow();
            for (var parameter : method.getParameters()) {
                var annotation = parameter.getAnnotation(QueryParam.class);
                if (annotation == null) continue;
                String key = annotation.value();
                results.add(execute(operation, Map.of(), "missing-" + key));
                results.add(execute(operation, Map.of(key, ""), "empty-" + key));
                results.add(execute(operation, Map.of(key, parameter.getType().equals(Integer.class)
                        ? "-150000" : "CMDB15 á & + / ? #"), "encoded-" + key));
            }
        }
        return results;
    }

    public List<Map<String, Object>> rutasInvalidas() throws Exception {
        var results = new ArrayList<Map<String, Object>>();
        for (JsonNode resource : TransporteHttpTesting.routes()) {
            String full = matrix.path("servlet_context").asText() + resource.path("path").asText();
            results.add(negative("PATCH", full, 405));
            results.add(negative("GET", full + "/extra-segment", 404));
            if (!full.toLowerCase(java.util.Locale.ROOT).equals(full)) results.add(negative("GET", full.toLowerCase(java.util.Locale.ROOT), 404));
        }
        results.add(negative("GET", matrix.path("servlet_context").asText() + "/recursos/cmdb15-no-existe", 404));
        return results;
    }

    private Map<String, Object> negative(String verb, String path, int expected) throws Exception {
        var response = client.send(HttpRequest.newBuilder(origin.resolve(path)).timeout(Duration.ofSeconds(10))
                .method(verb, HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.discarding());
        check(response.statusCode() == expected, "Unexpected invalid route/method status: " + verb + " " + path);
        check(response.headers().allValues("Location").isEmpty(), "Invalid route redirected");
        return Map.of("verb", verb, "full_path", path, "expected_status", expected, "status", response.statusCode());
    }
}
