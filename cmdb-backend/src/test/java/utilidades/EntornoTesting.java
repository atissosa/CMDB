package utilidades;

import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.errors.ExcepcionConfiguracion;
import com.atissosa.cmdb_backend.logic.Sistema;

public class EntornoTesting {
    public static Sistema getInstanciaSistemaTesting() {
        String config = System.getProperty("cmdb.test.config", System.getenv("CMDB_TEST_CONFIG"));
        if (config == null || config.isBlank()) {
            SqlServerTesting.configurar();
        } else {
            try {
                PropiedadesTesting.cargarPropiedadesTesting(config);
            } catch (ExcepcionConfiguracion ex) {
                throw new IllegalStateException("No se pudo preparar la configuración exclusiva de pruebas.", ex);
            }
        }
        Respuesta.cargarMensajes();
        return Sistema.getInstancia();
    }
}
