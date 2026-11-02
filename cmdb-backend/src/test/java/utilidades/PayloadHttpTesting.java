package utilidades;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Structural comparisons against frozen original JSON-B output; never records full production rows. */
public final class PayloadHttpTesting {
    private static final ObjectMapper JSON = new ObjectMapper();
    private final URI base;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final JsonNode baseline;

    public PayloadHttpTesting(URI base) throws Exception { this.base = base; baseline = PayloadFixtureObjects.baseline(); }
    private record Result(int status, String text, String contentType) {
        JsonNode json() throws Exception { return JSON.readTree(text); }
    }
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }
    private Result call(String method, String path, String payload, String auth) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create(base.toString().replaceAll("/+$", "") + "/recursos/" + path))
                .timeout(Duration.ofSeconds(20)).header("Content-Type", "application/json").header("Accept", "application/json");
        if (auth != null) builder.header("Authorization", auth);
        var response = client.send(builder.method(method, payload == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8)).build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new Result(response.statusCode(), response.body(), response.headers().firstValue("Content-Type").orElse(""));
    }
    private static void exact(Result response, int status, JsonNode expected, String label) throws Exception {
        check(response.status == status, label + ": HTTP " + response.status + " expected " + status);
        check(response.contentType.startsWith("application/json"), label + ": JSON Content-Type missing");
        if (!expected.equals(response.json())) throw new AssertionError(label + ": JSON structure differs; expected " + expected + ", observed " + response.json());
    }
    private JsonNode envelope(int code, String mode) {
        for (JsonNode item : baseline.path("envelopes")) if (item.path("code").asInt() == code && item.path("mode").asText().equals(mode)) return item.path("expected").deepCopy();
        throw new AssertionError("Missing frozen envelope");
    }

    public List<Map<String,Object>> outputs() throws Exception {
        var results = new ArrayList<Map<String,Object>>();
        for (JsonNode item : baseline.path("dto_cases")) for (String variant : List.of("full", "empty", "nullable")) {
            if (!item.path(variant).path("supported").asBoolean()) continue; // Original getter failure, not an API output shape.
            String name = item.path("class").asText();
            var response = call("GET", "cmdb16-fixtures/output/" + name + "/" + variant, null, null);
            exact(response, 200, item.path(variant).path("expected"), name + "/" + variant);
            results.add(Map.of("class",name,"variant",variant,"status",response.status,"structural_match",true));
        }
        var extra = call("GET", "cmdb16-fixtures/output/DTOFiltroEquipo/null_lists", null, null);
        exact(extra, 200, PayloadFixtureObjects.additional().path("null_list_output"), "DTOFiltroEquipo null lists");
        results.add(Map.of("class","DTOFiltroEquipo","variant","null_lists","status",200,"structural_match",true));
        check(results.size() == 54, "Expected all 54 supported original DTO output variants");
        return results;
    }

    public List<Map<String,Object>> inputs() throws Exception {
        var results = new ArrayList<Map<String,Object>>();
        var allInputs = new ArrayList<JsonNode>();
        baseline.path("input_cases").forEach(allInputs::add);
        PayloadFixtureObjects.additional().path("input_cases").forEach(allInputs::add);
        for (JsonNode item : allInputs) {
            String name = item.path("class").asText(), variant = item.path("variant").asText();
            var response = call("POST", "cmdb16-fixtures/input/" + name, item.path("input").toString(), null);
            if (item.path("accepted").asBoolean()) exact(response,200,item.path("expected_state"),name+" input "+variant);
            else safeBadRequest(response, name + " input " + variant);
            results.add(Map.of("class",name,"variant",variant,"status",response.status,"matches_legacy_acceptance",true));
        }
        check(results.size() == 93,"Expected all 93 original input examples");
        return results;
    }

    public List<Map<String,Object>> envelopes() throws Exception {
        var results = new ArrayList<Map<String,Object>>();
        for (JsonNode item : baseline.path("envelopes")) {
            int code = item.path("code").asInt();String mode=item.path("mode").asText();
            var response=call("GET","cmdb16-fixtures/envelope/"+code+"/"+mode,null,null);
            exact(response,item.path("http_status").asInt(),item.path("expected"),"envelope "+code+" "+mode);
            results.add(Map.of("business_code",code,"mode",mode,"http_status",response.status,"structural_match",true));
        }
        check(results.size()==60,"Expected all 30 business codes with null and empty-list returns");
        return results;
    }

    public Map<String,Object> realEndpoints() throws Exception {
        var account=TransporteHttpTesting.account();String auth=account.authorization();int id=0;
        try {
            var login=call("POST","login",null,auth);check(login.status==200 && login.json().path("codigo").asInt()==1,"Synthetic login failed");
            String name="CMDB16-"+UUID.randomUUID();String description="Descripción sintética á ñ";
            String data=JSON.writeValueAsString(Map.of("nombre",name,"descripcion",description));
            var created=call("POST","departamento",data,auth);check(created.status==200,"Create failed");
            id=created.json().path("retorno").path("id").asInt();check(id>0,"Create did not return a numeric ID");
            ObjectNode dto=JSON.createObjectNode().put("id",id).put("nombre",name).put("activo",true).put("textoAmostrar",name).put("descripcion",description);
            ObjectNode expected=(ObjectNode)envelope(5,"null");expected.set("retorno",dto);exact(created,200,expected,"production create DTO");
            expected=(ObjectNode)envelope(4,"empty_list");expected.set("retorno",JSON.createArrayNode().add(dto));
            exact(call("GET","departamento?id="+id,null,null),200,expected,"production read DTO");
            exact(call("POST","departamento","{}",auth),500,envelope(11,"null"),"production validation");
            exact(call("POST","departamento","null",auth),500,envelope(11,"null"),"production null entity");
            exact(call("GET","plantillaPC?marca=CMDB16_NONEXISTENT",null,null),200,envelope(12,"empty_list"),"production empty result");
            try(var input=PayloadHttpTesting.class.getResourceAsStream("/http/cmdb16/legacy-catalogs.json")) {
                JsonNode catalogs=JSON.readTree(input);
                for(var names=catalogs.fieldNames();names.hasNext();) {
                    String resource=names.next();exact(call("GET",resource,null,null),200,catalogs.path(resource),"production enum catalog "+resource);
                }
            }

            exact(call("POST","login",null,null),401,envelope(2,"null"),"production authentication");
            var restricted=TransporteHttpTesting.account(2);
            try {
                var session=call("POST","login",null,restricted.authorization());
                check(session.status==200 && session.json().path("codigo").asInt()==1,"Restricted fixture login failed");
                exact(call("POST","departamento",data,restricted.authorization()),403,envelope(13,"null"),"production authorization");
            } finally { call("DELETE","login",null,restricted.authorization()); }

            exact(call("DELETE","departamento","{\"id\":"+id+"}",auth),200,envelope(9,"null"),"production delete omitted return");id=0;
            return Map.of("created_read_deleted",true,"unicode_preserved",true,"validation_http",500,"missing_auth_http",401,"authorization_http",403,
                    "null_entity_preserved",true,"empty_list_preserved",true,"null_return_omitted",true);
        } finally {
            if(id>0)call("DELETE","departamento","{\"id\":"+id+"}",auth);
            call("DELETE","login",null,auth);
        }
    }

    public Map<String,Object> maintenance() throws Exception {
        var original=call("GET","prestamo",null,null);check(original.status==200,"Maintenance read failed");
        ObjectNode numeric=JSON.createObjectNode(), quoted=JSON.createObjectNode();
        for(String key:List.of("RCantMesesEnPrestamo","RCantVecesPrestado","ACantMesesEnPrestamo","ACantVecesPrestado","VCantMesesEnPrestamo","VCantVecesPrestado")) {numeric.put(key,16);quoted.put(key,"16");}
        try {
            exact(call("POST","mantenimientoPrestamo",quoted.toString(),null),200,envelope(6,"null"),"frontend capitalized numeric strings");
            JsonNode expected=PayloadFixtureObjects.additional().path("maintenance_expected");
            exact(call("GET","prestamo",null,null),200,expected,"frontend capitalized numeric response");
            return Map.of("capitalized_names_preserved",true,"numeric_strings_accepted",true,"json_numbers_returned",true);
        } finally {
            ObjectNode restoreDto=JSON.createObjectNode();int index=0;
            for(String key:List.of("RCantMesesEnPrestamo","RCantVecesPrestado","ACantMesesEnPrestamo","ACantVecesPrestado","VCantMesesEnPrestamo","VCantVecesPrestado"))
                restoreDto.set(key,original.json().path("retorno").get(index++));
            var restore=call("POST","mantenimientoPrestamo",restoreDto.toString(),null);
            check(restore.status==200 && restore.json().path("codigo").asInt()==6,"Maintenance fixture was not restored");
        }
    }

    public Map<String,Object> invalidEntities() throws Exception {
        for(String data:List.of("{","{\"precioDeCompra\":\"CMDB16_PRIVATE_MARKER\"}","{\"memoriaRam\":{\"CMDB16_PRIVATE_MARKER\":1}}"))
            safeBadRequest(call("POST","pc",data,null),"production malformed input");
        return Map.of("malformed_examples",3,"http_status",400,"values_or_internals_disclosed",false);
    }

    private static void safeBadRequest(Result response,String label) {
        check(response.status==400,label+": expected 400, observed "+response.status);
        check(response.text.equals("JSON inválido."),label+": unsafe or changed transport error text");
        check(!response.text.contains("CMDB16_PRIVATE_MARKER")&&!response.text.contains("java.")&&!response.text.contains("com.atissosa")&&!response.text.contains("stackTrace"),label+": internal details disclosed");
    }
}
