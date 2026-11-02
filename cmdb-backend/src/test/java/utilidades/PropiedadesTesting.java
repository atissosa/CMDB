package utilidades;

import com.atissosa.cmdb_backend.config.ConfiguracionAplicacion;
import com.atissosa.cmdb_backend.errors.ExcepcionConfiguracion;
import com.atissosa.cmdb_backend.global.Propiedades;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;

/** Optional external test database: Spring keys for non-secrets, canonical environment credentials. */
public class PropiedadesTesting extends Propiedades {
    public static void cargarPropiedadesTesting(String archivo) throws ExcepcionConfiguracion {
        try {
            var environment = new StandardEnvironment();
            environment.setActiveProfiles("test");
            environment.getPropertySources().addLast(new ResourcePropertySource("base", "classpath:application.properties"));
            environment.getPropertySources().addFirst(new ResourcePropertySource("test", "classpath:application-test.properties"));
            environment.getPropertySources().addFirst(new ResourcePropertySource("external-test", "file:" + archivo));
            Propiedades.configurar(new ConfiguracionAplicacion().configuracionBackend(environment));
        } catch (Exception ex) {
            // Keep supplied values out of nested binding/driver exception output.
            throw new ExcepcionConfiguracion("Configuración externa de pruebas inválida; use claves cmdb.* sin secretos y variables CMDB_DB_USERNAME/CMDB_DB_PASSWORD.", null);
        }
    }
}
