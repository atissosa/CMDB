# CMDB-19 — F14-01 Modernizar conexión a BD sin tocar SQL/SP

## Objetivo
Migrar la apertura de conexiones SQL Server desde `DriverManager` hacia un `DataSource` con pool (HikariCP) gestionado por Spring Boot, manteniendo consultas/SP y semántica transaccional existentes.

## Inventario de ciclo de vida JDBC
Inventario antes/después registrado en:
- `docs/evidence/cmdb19/jdbc-lifecycle-inventory.json`

Resumen:
- Antes: `BDSqlServer.conectar` cargaba driver y abría conexión con `DriverManager.getConnection` en cada operación.
- Después: `BDSqlServer.conectar` toma conexión desde `DataSource` configurado (`Hikari`).
- `Persistencia` mantiene el cierre por operación en `finally` (`bd.desconectar()`), que ahora devuelve la conexión al pool.
- `PreparedStatement`, `CallableStatement` y `ResultSet` conservan la estrategia previa de cierre.
- `commit/rollback` y `setAutoCommit(!transaccional)` se mantienen sin cambios de semántica.

## Implementación
### 1) DataSource con pool gestionado
- Se añadió `spring-boot-starter-jdbc` en `pom.xml` para auto-configuración de `DataSource`/Hikari.
- Se enlazó `spring.datasource.*` contra configuración `cmdb.bd.*` tipada.
- `ContextoAplicacion` inyecta `DataSource` y lo registra en la fachada legacy (`BD.configurarDataSource`).

### 2) Configuración tipada y por perfil
Se extendió `ConfiguracionBackend` con `cmdb.bd.pool.*` validado:
- `min-idle`, `max-size`
- `connection-timeout-ms`, `idle-timeout-ms`, `max-lifetime-ms`, `validation-timeout-ms`

Valores definidos por perfil:
- `application.properties`: baseline común
- `application-dev.properties`: baseline dev conservador
- `application-test.properties`: pool pequeño para pruebas deterministas
- `application-prod.properties`: baseline conservador sin tuning productivo

### 3) Adaptación de infraestructura legacy
- `BD` ahora mantiene referencia estática al `DataSource` activo.
- `BDSqlServer` ya no usa `DriverManager` ni carga manual de driver; toma conexiones del `DataSource`.
- Se creó `ConfiguradorDataSource` para bootstrap de escenarios no-Spring (compatibilidad de tests legacy vía `Propiedades.configurar`), sin cerrar `DataSource` por operación.

### 4) SQL/SP y reglas de negocio
No se modificaron textos SQL, firmas de stored procedures, esquema ni lógica de negocio.
Referencia:
- Hashes de mapeadores SQL Server: `docs/evidence/cmdb19/sql-sp-files-sha256.txt`
- Lista de archivos tocados: `docs/evidence/cmdb19/changed-files.txt`

## Validación
Comandos ejecutados:
- `./mvnw -version`
- `./mvnw -B clean verify`
- `python scripts/verify-test-inventory.py`

Resultado:
- Build: `BUILD SUCCESS`
- Tests: 152, fallos 0, errores 0, omitidos 0
- Inventario: base 131 preservados, añadidos 21, sin faltantes/inesperados/duplicados

Evidencia:
- `docs/evidence/cmdb19/maven-version.txt`
- `docs/evidence/cmdb19/build.log`
- `docs/evidence/cmdb19/inventory.json`
- `docs/evidence/cmdb19/TEST-PoolPersistenciaIntegrationTests.xml`
- `docs/evidence/cmdb19/TEST-TransporteRestTests.xml`

## Pruebas agregadas (F14)
Clase: `PoolPersistenciaIntegrationTests`
- Obtención/devolución de conexiones al pool
- Commit y rollback con persistencia/verificación real
- Liberación de recursos ante excepción
- Reutilización de pool pequeño sin agotamiento

Inventario actualizado en `docs/test-inventory-additions.json`.

## Riesgos y notas
- Hikari informa warning de `keepaliveTime >= maxLifetime` con configuración por defecto del framework; no afecta el resultado funcional de esta tarea.
- Se detectó un contenedor SQL Server persistente preexistente en host (`docker ps`) ajeno a esta ejecución; no se detuvo para no impactar el entorno del usuario.
