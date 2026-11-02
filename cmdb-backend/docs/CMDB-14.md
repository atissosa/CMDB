> Desde CMDB-15, la URL base predeterminada compatible con el frontend original es `/cmdb-backend/recursos/`. `/recursos` sigue siendo el prefijo Jersey relativo al contexto de aplicación. Los resultados de ejecución anteriores se conservan como históricos.

# CMDB-14 — F09-01: frontera REST de entrada/salida

## Resultado y alcance

La capa existente de CMDB-8 ya cumple la adaptación de transporte: **24 recursos,
51 operaciones** bajo `/recursos`, Jersey/Jakarta REST y delegación a `Sistema`.
No fue necesario reescribir controllers ni introducir otra capa o stack. Se
completa la evidencia que faltaba con seis pruebas HTTP reales y una validación
independiente del JAR ejecutable, incluyendo escrituras y lectura posterior.

Se conserva `src/main/java/com/atissosa/cmdb_backend/controllers` y su package.
`global.Aplicacion` registra explícitamente los 24 recursos y un FiltroContenedor;
su dependencia de ContextoAplicacion sigue garantizando configuración validada
antes de atender solicitudes. El JAR contiene una única clase de cada recurso.
No se añadieron reglas de negocio, SQL, servicios ni mapeadores en los controllers.
Los fuentes productivos y el POM quedan idénticos al respaldo previo a CMDB-14.

## Inventario antes/después

`docs/cmdb14-rest-matrix.json` registra para las 51 operaciones: recurso, ruta,
verbo, nombre de método, parámetros completos (incluidas anotaciones de query y
cabeceras), tipo de respuesta, JSON y métodos/argumentos delegados a Sistema.
Las matrices `evidence/cmdb14/rest-before.json` y `rest-after.json` son iguales.

`source-comparison.json` compara los 24 recursos y UtilidadesServicios contra
`Desktop/CMDB_ENTREGA/Fuentes/cmdb-backend/src/java/servicios`:
los 25 cuerpos son iguales al ignorar package/imports, espacios y comentarios.
La diferencia de namespaces javax → jakarta corresponde a CMDB-8, no a una
reescritura de esta tarea. Rutas/verbo y delegaciones se comparan aparte, sin
normalizarlas. Hay 51 pares ruta/verbo únicos y las 51 operaciones invocan lógica
legacy. Se conserva también el mapeo de Respuesta a HTTP de UtilidadesServicios.

La referencia de frontend `Desktop/CMDB_ENTREGA/Aplicacion/propiedades.js` declara
`recursosBackend` como base configurable terminada en `/recursos/`. El prefijo del
JAR sigue siendo `/recursos`; el contexto que un contenedor externo pudiera añadir
al desplegar el WAR no se confunde con las anotaciones de cada recurso. No se
modifica frontend ni se impone un contexto de aplicación nuevo.

## Pruebas de transporte añadidas

`src/test/java/com/atissosa/cmdb_backend/controllers/TransporteRestTests.java`
usa `@SpringBootTest(webEnvironment=RANDOM_PORT)`, perfil `test` y HttpClient del
JDK. No usa MVC/MockMvc ni mocks de Sistema. Los valores del SQL Server descartable
se registran por DynamicPropertySource reutilizando SqlServerTesting de CMDB-9/12/13.

| Método nuevo | Evidencia |
|---|---|
| registraLos24RecursosUnaSolaVez | Conjunto de clases @Path exacto, filtro registrado y OPTIONS con todos los verbos esperados en los 24 recursos |
| vinculaQueryParamsYSerializaLecturas | GET real de departamentos, selección por id y respuesta JSON; id no entero conserva HTTP 404 de conversión Jakarta |
| deserializaDTOYDelegaAltasCambiosYBajas | Login por cabecera; POST de DTODepartamentoCentral, GET posterior de los valores persistidos, PUT con nueva descripción, GET de confirmación y DELETE con cuerpo JSON; conserva códigos 5/6/9 y textos con á/ñ |
| vinculaCabecerasYConservaRespuestasDeLogin | Cabecera ausente e inválida → 401/código 2; Basic válido → 200/código 1; logout void → 204 |
| rechazaJsonInvalidoYTiposNoSoportados | JSON mal formado → 400 y tipo de contenido no soportado → 415, antes de delegar la entidad |
| conservaUnSoloFlujoCors | Respuesta del filtro existente sin duplicar cabeceras; se conserva el contrato actual |

`utilidades.TransporteHttpTesting` comparte las comprobaciones con el harness del
JAR para comprobar el mismo comportamiento empaquetado. Prepara cuentas sintéticas
únicas con rol/departamento del usuario de prueba administrador mediante SQL
parametrizado; esa preparación está exclusivamente en src/test, nunca en los
controllers. Evita depender de sesiones compartidas de user1 y las carreras legacy.
Los departamentos sintéticos creados se dan de baja por HTTP y las sesiones se
cierran; las cuentas temporales desaparecen al eliminar el contenedor. No se usan
credenciales de producción ni se registran Authorization o respuestas completas
que pudieran contener datos personales del dump.

## Inventario de tests

Los **131 métodos base de CMDB-9 se preservan**, incluido contextLoads. Se añaden
seis en `docs/test-inventory-additions.json`, con procedencia CMDB-14 y justificación.
`verify-test-inventory.py` suma ese inventario al original y comprueba también
entradas duplicadas del inventario; no reemplaza la lista base ni fija un conteo
que excluya pruebas nuevas. El total esperado actual es **137**. Conserva los
rechazos por fallos, errores, omisiones, faltantes, duplicados e inesperados.

## Ejecución reproducible

JDK 21.0.13.0.2 y Maven Wrapper 3.10.0; Docker accesible, SQL Server Developer
2022 CU20 descartable y fixtures de CMDB-9 saneados en CMDB-13. Red necesaria si
faltan imágenes o dependencias. No se configura ni utiliza un sistema externo.

```sh
./mvnw -version
./mvnw -B clean verify
python scripts/verify-test-inventory.py
./mvnw -B dependency:build-classpath -DincludeScope=test -Dmdep.outputFile=<archivo-temporal>
java --class-path 'target/test-classes:target/classes:<classpath-resuelto>' scripts/ValidarTransporte.java
```

El harness inicia otro SQL Server aislado y el JAR con perfil test, dirección
127.0.0.1 y puerto HTTP aleatorio. Inyecta las credenciales generadas con
CMDB_DB_USERNAME/CMDB_DB_PASSWORD en el entorno del hijo, nunca en argumentos o
archivos. Comandos exactos sin secretos y resultados están en `jar-command.json`
y `jar-http/results.json`; el log se comprueba contra la contraseña generada y
una coincidencia hace fallar la validación, aunque se sanee la evidencia.

Referencias oficiales consultadas:
[registro explícito Jersey en Boot](https://docs.spring.io/spring-boot/reference/web/servlet.html#web.servlet.jersey)
y [servidor real RANDOM_PORT para pruebas](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html).

## Resultados y límites

`clean verify`: **BUILD SUCCESS**, 1 min 36 s. Se ejecutaron **137/137** métodos
(131 base + 6 nuevos), 0 fallos/errores/omitidos; sin faltantes, duplicados o
inesperados. Los seis métodos nuevos pasaron. Reportes completos y cotejo en
`evidence/cmdb14/run1/`.

Las excepciones internas legacy ya documentadas pueden aparecer en utilidades
sin aserciones; el éxito de JUnit no demuestra su ausencia. Esta tarea no amplía
la lógica de negocio ni repara esos defectos. El aviso previo de WADL/JAXB opcional
no equivale a un error del proveedor JSON comprobado por las operaciones válidas.

La cobertura HTTP es representativa; la comprobación exhaustiva de DTOs, estados,
contratos y todas las ramas funcionales sigue en CMDB-15/16. No se confunde OPTIONS
con validación de escritura: se comprueban también cabeceras, JSON y lecturas de
lo efectivamente persistido. CORS y autenticación conservan su flujo actual; su
endurecimiento corresponde a CMDB-17/18, sin filtros nuevos ni rediseño aquí.

No hubo cambios de CI, despliegue, frontend, servicios, persistencia o fuentes
productivos. La carpeta no tiene Git: `backup.txt` identifica el respaldo previo;
`implementation.diff` y `changed-files.json` describen los cambios de pruebas y
scripts. El inventario y este documento constituyen documentación adicional.


## Resultado del JAR ejecutable

El mismo JAR generado por verify arrancó en localhost/perfil test y pasó las
seis comprobaciones compartidas con JUnit: los 24 recursos y sus verbos,
query params y lectura por id, POST/PUT/DELETE con lectura persistida de un
DTODepartamentoCentral, cabeceras Basic/login/logout, JSON inválido y media type,
y CORS sin duplicados. Las respuestas conservaron HTTP 200 y códigos de negocio
5/6/9 para escrituras, 401/2 para login inválido, 204 para logout, 400/415 en
entidades inválidas y 404 en query de entero mal formado.

`jar-http/results.json` registra resultados y la parada de la aplicación.
`artifact.json` conserva el SHA-256 del JAR y confirma 24 clases REST únicas y
exclusión de pruebas/fixtures del artefacto productivo. El secreto generado no
aparece en la salida de la aplicación. Los dos SQL Server y dos Ryuk creados
para suite/harness fueron eliminados; `cleanup.json` verifica los cuatro IDs.
El código productivo sigue sin cambios frente al respaldo previo.

Registro `CMDB14-20261101T231957Z`: 2026-11-01T23:19:57.830000+00:00 a 2026-11-01T23:30:24.588882+00:00, 627 s. Sesión Codex `01a196e5-81ec-7970-925b-7dab80c23a50`. Estimación vigente S/20 minutos (reestimación indicada en Plane); anterior M/30–60 minutos. Cuota antes/después: 28.0%/37.0% usado en ventana de cinco horas, 48.0%/49.0% semanal. Observaciones de sesión, no consumo exclusivo. No hay medición exclusiva de tokens de esta tarea.
