package com.atissosa.cmdb_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import utilidades.SqlServerTesting;

@SpringBootTest
@ActiveProfiles("test")
class CmdbBackendApplicationTests {

    @DynamicPropertySource
    static void configuracionAislada(DynamicPropertyRegistry registry) {
        SqlServerTesting.registrarPropiedades(registry);
    }

	@Test
	void contextLoads() {
	}

}
