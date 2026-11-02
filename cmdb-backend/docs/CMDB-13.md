> Desde CMDB-15, la URL base predeterminada compatible con el frontend original es `/cmdb-backend/recursos/`. `/recursos` sigue siendo el prefijo Jersey relativo al contexto de aplicación. Los resultados de ejecución anteriores se conservan como históricos.

# CMDB-13 — F08-01: secretos externos

## Contrato único

| Ambiente | Usuario SQL Server | Contraseña SQL Server | Origen permitido |
|---|---|---|---|
| dev | CMDB_DB_USERNAME | CMDB_DB_PASSWORD | Entorno del proceso |
| prod | CMDB_DB_USERNAME | CMDB_DB_PASSWORD | Entorno del proceso |
| test, BD externa | CMDB_DB_USERNAME | CMDB_DB_PASSWORD | Entorno del proceso |
| test, suite aislada | Mismas claves canónicas | Mismas claves canónicas | DynamicPropertySource de Spring alimentado por Testcontainers |

`application.properties` mapea explícitamente estas variables a
`cmdb.bd.usuario` y `cmdb.bd.contrasenia`. No hay credenciales predeterminadas.
`ConfiguracionSecretos` usa las PropertySource de Spring, de forma centralizada,
y verifica el origen y los valores antes de inicializar la fachada legacy.
Los nombres anteriores de CMDB-12 ya no son alternativas: `CMDB_BD_USUARIO`,
`CMDB_BD_CONTRASENIA`, propiedades directas con secretos, archivos con credenciales,
propiedades Java y argumentos CLI de credenciales no se admiten. Los archivos
externos pueden repetir el mapeo canónico, pero no aportar otro valor secreto.

La presencia de usuario/contraseña es obligatoria y su valor no puede estar vacío
ni ser solo espacios; se rechazan placeholders literales identificables como
`changeme`, `${...}` o `<...>`. Una contraseña válida no se recorta ni transforma.
El mensaje de configuración nombra la variable y nunca su contenido. Los datos
no sensibles conservan la precedencia de CMDB-12: archivo < entorno < Java < CLI.
Las credenciales tienen una sola fuente externa; la excepción programática se
limita al perfil test y a la fuente dinámica estándar usada por el contenedor.
No se incorporan gestores de secretos, perfiles paralelos ni archivos .env.

## Protección del adaptador y salidas

La fachada `Propiedades` conserva sus consumidores legacy. `BDSqlServer` construye
una URL **sin usuario ni contraseña** y entrega ambos valores al driver en una
colección `Properties`, evitando problemas de delimitadores y exposición en URLs.
Los errores de conexión/autenticación dejan de encadenar la excepción del driver,
que puede incluir el usuario o valores suministrados: se conserva una excepción
controlada con mensaje genérico. No cambia la respuesta funcional de error de BD
(código 3 / HTTP 500) ni los endpoints.

Los records de configuración ya ocultaban los datos de BD en `toString`; se
comprueba de nuevo con marcadores. El log de inicialización de CMDB-12 sigue
mostrando solo datos no sensibles. No existe una dependencia Actuator ni se añadió
un endpoint para volcar configuración. DTOUsuario no expone la contraseña.

Referencia oficial: [Microsoft recomienda credenciales en la colección de
propiedades](https://learn.microsoft.com/en-us/sql/connect/jdbc/building-the-connection-url?view=sql-server-ver17).
La resolución de placeholders y fuentes sigue la
[configuración externa de Spring Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html).

## Inventario de superficies revisadas

- Configuración: application*.properties, ConfiguracionBackend,
  ConfiguracionAplicacion, ContextoAplicacion y Propiedades. Solo las dos claves
  de BD anteriores son secretos externos requeridos. Proveedor, host, puerto,
  nombre de BD, login y departamento son configuración no secreta.
- Persistencia: BD/BDSqlServer consumen las credenciales de conexión; no contienen
  literales operativos. MapeadorUsuarioSqlServer/Usuario/LoginClasico manejan las
  contraseñas funcionales de los usuarios de la aplicación desde la BD; no son
  tokens o secretos externos nuevos. No se modifica esa autenticación en esta tarea.
- Recursos/diagnóstico: DTOs y recursos existentes, sin volcado de configuración.
  No se inventan variables para tokens o capacidades ausentes.
- Tests: SqlServerTesting genera una contraseña por instancia y la inyecta con las
  claves canónicas al contexto test. La opción de BD externa de PropiedadesTesting
  migra al mismo Binder/configuración de CMDB-12: archivo de claves Spring solo
  no sensibles más las variables canónicas. Se actualiza la plantilla sin secretos.
- Fixtures: `src/test/resources/db/legacy/02_datos.sql` contenía diez filas de
  usuarios. Dos son cuentas sintéticas del escenario de tests; se mantienen para
  conservar las aserciones. Ocho identidades importadas con contraseñas de apariencia
  reutilizable no tienen referencias literales en las pruebas Java: se sustituyen
  por nombres/contraseñas claramente sintéticos, conservando IDs, roles y relaciones.
  Su posible vigencia externa no se verificó ni se usaron contra otro sistema.
  `fixture-sanitization.json` registra filas y acciones, sin valores originales.
- Scripts: se actualiza ValidarPerfiles al contrato canónico y se añade
  ValidarSecretos para la matriz independiente. Ninguno contiene credenciales reales.
- Archivos auxiliares, documentación y evidencias históricas de la copia activa:
  escaneo de texto, patrones conocidos de tokens/claves privadas y revisión de
  contextos de credenciales. No se encontraron otros secretos requeridos por el
  backend. Un valor histórico puede coincidir con texto ordinario de rutas o
  versiones: esas coincidencias no se clasifican por sí solas como credenciales.
  `repository-scan.json` registra alcance y hallazgos sin valores. El escaneo
  heurístico no equivale a certificar todos los formatos posibles de secreto.

`.gitignore` excluye .env locales, archivos *.secrets.properties y
config-local.properties. No se crea ninguno con secretos; solo se distribuye una
plantilla sin valores operativos. La carpeta no tiene repositorio Git.

## Inyección segura local y en CI/CD

Seleccionar un ambiente y configurar los datos no sensibles según CMDB-12.
En una terminal interactiva se pueden leer las credenciales sin dejarlas en
historial, argumentos del proceso o texto de un script:

```sh
read -r -p 'Usuario SQL Server: ' CMDB_DB_USERNAME
read -r -s -p 'Contraseña SQL Server: ' CMDB_DB_PASSWORD
export CMDB_DB_USERNAME CMDB_DB_PASSWORD
export SPRING_PROFILES_ACTIVE=dev
# CMDB_BD_SERVIDOR, CMDB_BD_PUERTO y CMDB_BD_NOMBRE ya configurados.
java -jar target/cmdb-backend-0.0.1-SNAPSHOT.jar
unset CMDB_DB_USERNAME CMDB_DB_PASSWORD
```

No usar `echo`, argumentos `-D`/`--`, archivos versionables o tracing de shell
para transmitir las credenciales. En CI/CD, el proveedor no está definido:
registrar ambas como variables protegidas/secretas del proveedor e inyectarlas
en el entorno del proceso Java. El comando sigue siendo `java -jar ...`, sin
valores en su texto. No se crea ni modifica ningún pipeline.

Para la BD externa **exclusiva de tests**, `CMDB_TEST_CONFIG` o
`-Dcmdb.test.config` identifica el archivo de datos no sensibles con claves
Spring. Se requieren las mismas variables de credenciales; el archivo antiguo
con BD_USUARIO/BD_CONTRASENIA deja de ser válido como fuente de secretos.
Sin esa selección explícita la suite conserva el contenedor descartable.

## Validación reproducible

```sh
./mvnw -version
./mvnw -B clean verify
python scripts/verify-test-inventory.py
./mvnw -B dependency:build-classpath -DincludeScope=test -Dmdep.outputFile=<archivo-temporal>
java --class-path 'target/test-classes:target/classes:<classpath-resuelto>' scripts/ValidarSecretos.java
```

Java 21 y Docker accesible, con red si faltan dependencias/imágenes. El helper
inicia SQL Server Developer 2022 CU20 con fixtures saneados. La matriz usa otro
contenedor y el mismo JAR para dev/prod. Las credenciales generadas o marcadores
solo viven en memoria y entorno del proceso hijo; no se escriben en archivos de
configuración, argumentos, resultados o evidencia. Los archivos externos de la
matriz contienen solo datos no sensibles. Las salidas se comparan en memoria con
los marcadores conocidos: si hay fuga, se marca fallo y la evidencia se sanea,
sin publicar el valor. No se da por válida una salida solo por haberla redactado.

## Límites y seguimiento histórico

La entrega original de Desktop/CMDB_ENTREGA y los respaldos quedan intactos. El
respaldo previo a esta tarea está fuera de la copia activa; su ruta consta en
`evidence/cmdb13/backup.txt`. La eliminación de las ocho credenciales importadas
no demuestra su desaparición de esas copias ni de servicios externos. Registrar
seguimiento para que el responsable del sistema origen determine vigencia y
necesidad de rotación/revocación de las credenciales históricas, sin valores en
Plane. Esta ejecución no accede, rota ni modifica credenciales de producción ni
reescribe historial. El diff de fixtures se conserva con valores originales
redactados para que la propia evidencia no los vuelva a introducir.

Se mantienen las limitaciones legacy ya documentadas en CMDB-9 y CMDB-12; una
suite exitosa no implica ausencia de errores internos o seguridad integral de
la autenticación funcional preexistente.


## Resultado de la ejecución

Java 21.0.13.0.2 / Maven Wrapper 3.10.0. `clean verify`: **BUILD SUCCESS**
(1 min 32 s), **131/131 métodos**, 0 fallos, errores y omisiones; sin faltantes,
duplicados ni inesperados. No se filtraron pruebas ni se cambiaron aserciones.
Reportes completos y cotejo en `evidence/cmdb13/run1/`. El contexto Spring test
resolvió las claves canónicas desde el contenedor mediante DynamicPropertySource.

Matriz del mismo JAR: **8/8 escenarios correctos**:

| Escenario | Comprobación |
|---|---|
| dev y prod (dos casos) | Arranque; configuración aplicada; OPTIONS 200; lectura de cuatro departamentos, HTTP 200/código 4 |
| usuario ausente | Salida 1, nombra CMDB_DB_USERNAME, sin inicialización legacy |
| contraseña ausente | Salida 1, nombra CMDB_DB_PASSWORD, sin inicialización legacy |
| usuario vacío | Salida 1, nombra CMDB_DB_USERNAME, sin inicialización legacy |
| contraseña vacía | Salida 1, nombra CMDB_DB_PASSWORD, sin inicialización legacy |
| alias anterior | Salida 1, rechazo explícito, sin valores en mensaje |
| credencial inválida sintética | Inicio configurado; lectura falla de forma controlada con HTTP 500/código 3, sin mensaje original del driver ni credenciales |

La prueba adicional comprobó preservación exacta de una contraseña con espacios,
URL JDBC sin credenciales y toString sin contraseña. Se revisaron las salidas de
los procesos hijos después de terminar y los archivos de evidencia actuales y
reportes Surefire con cinco marcadores conocidos, generados en memoria:
**ninguna coincidencia**. El reporte solo contiene conteo y rutas, no valores.
`matrix/results.json`, `matrix/secret-scan.json`, logs individuales y comandos en
`matrix-command.json` conservan la evidencia. Se corrigió un acceso protegido del
harness mediante reflexión antes de ejecutar la matriz; ese fallo de compilación
del harness no afectó al backend ni requirió cambiar sus pruebas.

El JAR incluye los mapeos canónicos y excluye infraestructura/fixtures de tests;
SHA-256 y ruta en `artifact.json`. Las dos instancias SQL Server y sus Ryuk fueron
eliminadas, y todas las aplicaciones detenidas (`cleanup.json`).

El inventario de texto abarcó las superficies activas, sin patrones conocidos de
claves/tokens adicionales ni asignaciones del secreto histórico de BD. Las ocho
credenciales importadas originales ya no aparecen en texto activo según el cotejo
puntual. Esto no certifica ausencia de todos los posibles formatos de secretos.
El diff de código está en `implementation.diff`, con las filas históricas saneadas;
es evidencia revisable, no un parche íntegro de recuperación. El respaldo fuera
de la copia activa permite recuperación por un operador autorizado.

Seguimiento registrado y verificado: **CMDB-23**, para evaluar
posible exposición en copias históricas y la remediación por su responsable, sin
credenciales en la tarea. No se confirmaron ni modificaron accesos externos.

Registro `CMDB13-20261101T120831Z`: 2026-11-01T12:08:31.593000+00:00 a 2026-11-01T12:21:34.404362+00:00, 783 s. Sesión `01a196e5-81ec-7970-925b-7dab80c23a50`. Cuota observada antes/después: 74.0%/86.0% usado en ventana de cinco horas, 39.0%/41.0% semanal. Observaciones de sesión, no consumo exclusivo de esta tarea. IDs de runner original/anterior no disponibles; ejecución manual asistida sin continuación.
