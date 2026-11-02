package Equipo;

import com.atissosa.cmdb_backend.logic.Sistema;
import org.junit.jupiter.api.Test;import utilidades.EntornoTesting;

public class FormatearCodigosDeEquiposEnBDTest {
    
    public FormatearCodigosDeEquiposEnBDTest() {
    }
    
    @Test
    public void setUp() {
        Sistema s = EntornoTesting.getInstanciaSistemaTesting();
        s.formatearCodigosDeEquiposBD();
    }
    
}
