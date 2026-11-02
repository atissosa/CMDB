# CMDB-21 — F16-01 Migrar pruebas a JUnit Jupiter y eliminar rutas rigidas

## Objetivo
Migrar la suite legacy de pruebas a JUnit Jupiter/Platform (gestionado por Spring Boot 4.1.2) y eliminar dependencias de rutas locales rigidas, conservando inventario, cobertura y ejecucion aislada.

## Cambios aplicados

### 1) Migracion JUnit 4 -> Jupiter en `src/test/java`
- Reemplazo de imports/anotaciones JUnit 4 por Jupiter:
  - `org.junit.Test` -> `org.junit.jupiter.api.Test`
  - `@BeforeClass/@AfterClass` -> `@BeforeAll/@AfterAll`
  - `@Before/@After` -> `@BeforeEach/@AfterEach`
  - `org.junit.Assert.*` -> `org.junit.jupiter.api.Assertions.*`
- Se mantuvieron paquetes, estructura de `src/test/java` y casos de prueba.

### 2) Dependencias de test
- Se retiraron del `pom.xml`:
  - `junit:junit`
  - `org.junit.vintage:junit-vintage-engine`
- Se conserva el stack de test gestionado por el parent actual (Jupiter 6.x via `spring-boot-starter-test`).

### 3) Suite agregadora legacy
- `conjuntoDePruebas/ejecutarTodasLasPruebasDeBusquedas.java` se mantiene como referencia historica (sin runner JUnit4) para evitar doble descubrimiento en Platform.

### 4) Portabilidad de rutas
- Se sustituyeron rutas absolutas locales en `src/test/resources/http/cmdb15-contract.json` por rutas legacy-relativas (`Fuentes/...`).
- Escaneo final sin rutas locales absolutas en `src/test/java` y `src/test/resources`.

### 5) Estabilizacion de ejecucion equivalente
Sin cambiar reglas de negocio ni contratos HTTP, se ajustaron fixtures legacy de test para evitar inestabilidad entre ejecuciones limpias:
- `src/test/resources/junit-platform.properties`:
  - orden por nombre de clase y metodo en Jupiter para mantener comportamiento estable legacy.
- `utilidades/FuncionalidadesComunes.java`:
  - `generarSNrandom()` acota longitud y mantiene unicidad.
  - `crearDTOEquipoValido(...)` usa `numeroPrecinto` derivado del SN para evitar colisiones de datos de prueba.
- Reautenticacion por prueba en clases legacy con mayor sensibilidad de estado:
  - `Equipo/pruebaBajaEquipoTest`
  - `Equipo/PC/pruebaModificarPCTest`
  - `Equipo/pruebaPrestarEquipoTest`
  - `Equipo/pruebaDevolverEquipoACentralTest`

## Validacion y evidencia

### Baseline pre-migracion
- Versiones:
  - `docs/evidence/cmdb21/pre/maven-version.txt`
- Verify completo pre:
  - `docs/evidence/cmdb21/pre/mvn-clean-verify.txt`
- Inventario pre:
  - `docs/evidence/cmdb21/pre/verify-test-inventory.json`
- Dependencias pre (incluye JUnit4/Vintage):
  - `docs/evidence/cmdb21/pre/dependency-tree-junit.txt`
- Reportes Surefire pre:
  - `docs/evidence/cmdb21/pre/surefire-reports/`

### Post-migracion
- Verify 1 (limpio):
  - `docs/evidence/cmdb21/post/run1/mvn-clean-verify.txt`
  - `docs/evidence/cmdb21/post/run1/verify-test-inventory.json`
  - `docs/evidence/cmdb21/post/run1/surefire-reports/`
- Verify 2 (limpio, consecutivo):
  - `docs/evidence/cmdb21/post/run2/mvn-clean-verify.txt`
  - `docs/evidence/cmdb21/post/run2/verify-test-inventory.json`
  - `docs/evidence/cmdb21/post/run2/surefire-reports/`
- Dependencias post (solo Jupiter/Platform):
  - `docs/evidence/cmdb21/post/dependency-tree-junit.txt`
- Escaneo JUnit4/Vintage residual:
  - `docs/evidence/cmdb21/post/junit4-scan.txt`
- Escaneo de rutas rigidas:
  - `docs/evidence/cmdb21/post/rigid-paths-scan.txt`
- Comparacion caso-a-caso pre vs post:
  - `docs/evidence/cmdb21/post/pre-post-case-compare.json`

### Verificacion desde directorio distinto
- Comando ejecutado desde `/tmp` usando wrapper absoluto y `-f` al `pom.xml`.
- Evidencia:
  - `docs/evidence/cmdb21/post/cross-cwd/mvn-clean-verify-from-tmp.txt`

## Resultado
- Se preservan 131 metodos base + 25 agregados trazables: `156` esperados/ejecutados.
- Verificador de inventario post sin faltantes, duplicados, inesperados ni omitidos.
- Sin dependencia de `junit:junit` ni `junit-vintage-engine`.
- Ejecucion real sobre JUnit Jupiter/JUnit Platform.

## Notas
- Se mantuvo SQL Server descartable por Testcontainers y perfiles de testing vigentes (sin BD fija ni mocks sustitutivos).
- Se conservaron utilidades legacy de documentacion/mantenimiento; no se ocultaron defectos mediante `@Disabled` o filtros.
