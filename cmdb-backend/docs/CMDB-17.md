# CMDB-17 — F12-01 Aplicar CORS compatible con el frontend actual

## Objetivo
Aplicar una politica CORS valida para navegador, configurable por ambiente y compatible con el frontend legacy, sin origenes abiertos ni reflexion arbitraria de Origin.

## Inventario frontend (legacy)
Fuente inspeccionada sin modificar:
- `${HOME}/Desktop/CMDB_ENTREGA/Fuentes/cmdb-frontend/public_html/conf/propiedades.js`
- `${HOME}/Desktop/CMDB_ENTREGA/Fuentes/cmdb-frontend/public_html/js/funciones.js`

Hallazgos:
- Base backend configurada: `http://localhost:8081/cmdb-backend/recursos/`.
- Metodos usados desde frontend: `GET`, `POST`, `PUT`, `DELETE`.
- Content type efectivo: `application/json`.
- Header de autenticacion: `Authorization` en `beforeSend` cuando la llamada pasa credenciales.
- Uso de cookies: cookies locales para estado de UI/sesion del frontend; no se observo `withCredentials` para auth backend.

Inventario estructurado: `docs/evidence/cmdb17/frontend-inventory.json`.

## Implementacion
Se reemplazo la salida CORS invalida (`Access-Control-Allow-Origin: *` + `Access-Control-Allow-Credentials: true`) por una politica tipada y explicita por perfil:

- Unico mecanismo CORS: filtro Jersey `FiltroContenedor` (sin duplicados).
- El filtro ahora:
  - Evalua `Origin` contra allowlist exacta.
  - No refleja origenes no permitidos.
  - No emite wildcard con credenciales.
  - Responde con origen explicito permitido.
  - Agrega `Vary: Origin` y, para preflight, tambien `Vary: Access-Control-Request-Method` y `Vary: Access-Control-Request-Headers`.
  - Aplica la misma politica sobre respuestas exitosas y de error (al ser `ContainerResponseFilter`).
- `OPTIONS` preflight se procesa sin requerir sesion de negocio.

### Configuracion tipada (CMDB-12)
Se extendio `ConfiguracionBackend` con `Cors`:
- `cmdb.cors.allowed-origins`
- `cmdb.cors.allowed-methods`
- `cmdb.cors.allowed-headers`
- `cmdb.cors.allow-credentials`
- `cmdb.cors.max-age-seconds`

Validaciones:
- Origenes CORS deben ser URI validos con `http/https`, host y puerto explicitos, sin path/query/fragment/userinfo.
- No se admite `*` en `allowed-origins`.
- Si `allow-credentials=true`, la allowlist no puede estar vacia.
- En `prod`, la allowlist no puede quedar vacia (evita apertura silenciosa).

## Politica por perfil
- `application-dev.properties`:
  - `cmdb.cors.allowed-origins=http://localhost:8080,http://127.0.0.1:8080`
- `application-test.properties`:
  - `cmdb.cors.allowed-origins=http://localhost:8080,http://127.0.0.1:8080`
- `application-prod.properties`:
  - `cmdb.cors.allowed-origins=${CMDB_CORS_ALLOWED_ORIGINS:}` (obligatoria por validacion)
- comunes en `application.properties`:
  - `cmdb.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,HEAD`
  - `cmdb.cors.allowed-headers=origin,content-type,accept,authorization`
  - `cmdb.cors.allow-credentials=false`
  - `cmdb.cors.max-age-seconds=600`

## Validacion
Comandos ejecutados:
- `./mvnw -B clean verify`
- `python scripts/verify-test-inventory.py`
- `./mvnw -version`

Resultados:
- Build: `BUILD SUCCESS`.
- JUnit: 148 tests, 0 fallos, 0 errores, 0 omitidos.
- Inventario: `expected_methods=148`, `tests=148`, sin faltantes/duplicados/inesperados.
- Verificacion CORS automatizada (integracion):
  - origen permitido con preflight y `Authorization`: PASS
  - origen denegado: PASS (sin header allow-origin)
  - peticion sin `Origin`: PASS (sin headers CORS)
  - respuesta de error HTTP con origen permitido: PASS (mantiene CORS)

Matriz: `docs/evidence/cmdb17/cors-matrix.json`.
Reporte: `docs/evidence/cmdb17/TEST-TransporteRestTests.xml`.

## Limitacion registrada
No fue posible completar en esta ejecucion la comprobacion manual en navegador contra instancia dev viva porque `spring-boot:run` fallo por variables obligatorias ausentes (`CMDB_DB_USERNAME`/`CMDB_DB_PASSWORD`) en este entorno de ejecucion.

Evidencia del bloqueo:
- `docs/evidence/cmdb17/browser-check.json`
- `docs/evidence/cmdb17/build.log`

## Evidencia generada
- `docs/evidence/cmdb17/maven-version.txt`
- `docs/evidence/cmdb17/build.log`
- `docs/evidence/cmdb17/inventory.json`
- `docs/evidence/cmdb17/TEST-TransporteRestTests.xml`
- `docs/evidence/cmdb17/frontend-inventory.json`
- `docs/evidence/cmdb17/cors-matrix.json`
- `docs/evidence/cmdb17/browser-check.json`
