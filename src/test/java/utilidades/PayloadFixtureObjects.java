package utilidades;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/** Synthetic values match the independently generated legacy baseline, not current serializer output. */
public final class PayloadFixtureObjects {
    private static final ObjectMapper TREE = new ObjectMapper();
    private PayloadFixtureObjects() { }

    public static Class<?> type(String name) throws Exception {
        for (JsonNode item : baseline().path("dto_cases")) {
            if (item.path("class").asText().equals(name))
                return Class.forName("com.atissosa.cmdb_backend.dto." + name);
        }
        throw new IllegalArgumentException("Unknown synthetic fixture class");
    }

    public static JsonNode additional() throws Exception {
        try (var input = PayloadFixtureObjects.class.getResourceAsStream("/http/cmdb16/legacy-null-lists.json")) {
            if (input == null) throw new AssertionError("Missing additional original null-list baseline");
            return TREE.readTree(input);
        }
    }

    public static JsonNode baseline() throws Exception {
        try (var input = PayloadFixtureObjects.class.getResourceAsStream("/http/cmdb16/legacy-payloads.json")) {
            if (input == null) throw new AssertionError("Missing frozen legacy payload baseline");
            return TREE.readTree(input);
        }
    }

    private static Object empty(Class<?> c) throws Exception {
        if (c.getSimpleName().equals("Mensaje")) return c.getConstructors()[0].newInstance(null, null);
        return c.getConstructor().newInstance();
    }

    private static List<Field> fields(Class<?> c) {
        var fields = new ArrayList<Field>();
        for (; c != null && c != Object.class; c = c.getSuperclass()) {
            for (var field : c.getDeclaredFields()) if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                fields.add(field);
            }
        }
        return fields;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object object(String name, String variant) throws Exception {
        Class<?> c = type(name);
        Object value = empty(c);
        if (variant.equals("nullable") && name.equals("DTOFiltroEquipo"))
            c.getMethod("setEsPrestamo", Boolean.class).invoke(value, false);
        if (variant.equals("null_lists")) {
            for (var field : fields(c)) field.set(value, field.getName().equals("esPrestamo") ? false : null);
            return value;
        }
        if (!variant.equals("full")) return value;
        for (var field : fields(c)) {
            Class<?> t = field.getType();
            String n = field.getName();
            Object v;
            if (t == String.class) v = n.equals("fechaAlta") ? "2020-02-29" : n.equals("fecha") ? "29/02/2020 12:34:56.000"
                    : n.equals("tipo") ? "PC" : n.equals("estado") ? "OPERATIVO" : n.equals("ip") ? "127.0.0.1" : "Contrato á ñ";
            else if (t == Integer.class || t == int.class) v = 16;
            else if (t == Double.class || t == double.class) v = 12.5;
            else if (t == Boolean.class || t == boolean.class) v = true;
            else if (t.isEnum()) v = Enum.valueOf((Class) t, "OK");
            else if (List.class.isAssignableFrom(t)) v = new ArrayList<>(field.getGenericType().getTypeName().contains("Integer")
                    ? List.of(16,17) : List.of("Contrato á ñ"));
            else throw new IllegalStateException("Unexpected fixture field type");
            field.set(value, v);
        }
        return value;
    }

    public static ObjectNode state(Object value) {
        ObjectNode state = TREE.createObjectNode();
        try {
            for (var field : fields(value.getClass())) state.set(field.getName(), TREE.valueToTree(field.get(value)));
        } catch (IllegalAccessException ex) { throw new IllegalStateException("Could not inspect test fixture", ex); }
        return state;
    }
}
