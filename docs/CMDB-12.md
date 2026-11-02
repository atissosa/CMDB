> Desde CMDB-15, la URL base predeterminada compatible con el frontend original es `/cmdb-backend/recursos/`. `/recursos` sigue siendo el prefijo Jersey relativo al contexto de aplicación. Los resultados de ejecución anteriores se conservan como históricos.

> Actualizado por CMDB-13: credenciales únicamente por CMDB_DB_USERNAME/CMDB_DB_PASSWORD; archivos externos solo para datos no sensibles. La opción de BD externa de tests usa claves Spring y el mismo contrato. Los reportes de CMDB-12 conservan su carácter histórico.

# CMDB-12 — F07-01: configuración por ambientes

## Resolución e inicialización

El backend usa la resolución estándar de Spring Boot y `Binder` para crear el
record tipado `config.ConfiguracionBackend`. `ConfiguracionAplicacion` exige
exactamente un ambiente **explícitamente activo**, `dev`, `test` o `prod`, antes
de enlazar y validar valores. Otros perfiles auxiliares pueden coexistir, pero
no sustituyen al ambiente. No hay ambiente predeterminado: ni `default` ni un
`spring.profiles.default` implícito satisfacen la selección explícita.

Se comprueban campos obligatorios, enums, puerto entre 1 y 65535 y tipos booleanos.
Los errores nombran la clave y no imprimen su valor ni causas de binding que
puedan revelar credenciales. Los records que contienen credenciales ocultan sus
valores en `toString`. La ausencia de configuración aborta el contexto; no se
aceptan valores nulos silenciosos.

`ContextoAplicacion` pasa a ser un componente Spring: recibe la configuración ya
validada, aplica la fachada `Propiedades` y carga los mensajes de respuesta.
`Aplicacion` (ResourceConfig) depende de ese componente antes de registrar los
24 recursos. Así los recursos no pueden crear `Sistema` ni alcanzar persistencia
o autenticación antes de que la configuración esté disponible. La creación de
Sistema sigue siendo diferida hasta su uso; no se añadió una conexión al arrancar.
No se modifican sus reglas de negocio. El registro de arranque confirma solo
valores no sensibles y `legacyReady=true`, sin usuario, contraseña ni URL JDBC.

Se retiraron del código productivo el listener Servlet, `getResourceAsStream`
y la ruta fija `WEB-INF/conf.properties`. No hay fallback a la entrega legacy.
La fachada mantiene los campos que consumen las clases existentes. Solo adapta
el puerto tipado y las opciones TLS al ensamblador JDBC legacy existente.

## Inventario y mapeo

Se revisaron las lecturas de configuración en todas las fuentes productivas.
El proveedor lo consumen BD y las factorías de mapeadores; host, puerto, nombre,
usuario y contraseña los consume BD; el tipo de login, SubSistemaUsuarios; el
departamento, SubSistemaCMDB. No se encontraron otras propiedades externas leídas
por el backend. La ruta del antiguo archivo era un mecanismo de carga, no un dato
funcional. Las variables exclusivas de pruebas se describen aparte.

| Clave legacy | Propiedad Spring / variable de entorno | Tipo | Obligación y valor base |
|---|---|---|---|
| BD_PROVEEDOR | cmdb.bd.proveedor / CMDB_BD_PROVEEDOR | enum MSSQLSERVER | Requerida; común: MSSQLSERVER |
| BD_SERVIDOR | cmdb.bd.servidor / CMDB_BD_SERVIDOR | String no vacío | Requerida externamente en todos los perfiles; sin default |
| BD_PUERTO | cmdb.bd.puerto / CMDB_BD_PUERTO | Integer 1–65535 | Requerida; común: 1433, sin sufijos JDBC en esta clave |
| BD_BD | cmdb.bd.nombre / CMDB_BD_NOMBRE | String no vacío | Requerida externamente; sin default |
| BD_USUARIO | cmdb.bd.usuario / CMDB_DB_USERNAME | String no vacío | Requerida externamente; sin default; sensible |
| BD_CONTRASENIA | cmdb.bd.contrasenia / CMDB_DB_PASSWORD | String no vacío | Requerida externamente; sin default; secreta |
| LOGIN_TIPO | cmdb.login.tipo / CMDB_LOGIN_TIPO | enum CLASICO, ACTIVE_DIRECTORY | Requerida; común: CLASICO |
| DEPARTAMENTO_DEFECTO | cmdb.departamento.defecto / CMDB_DEPARTAMENTO_DEFECTO | String no vacío | Requerida; común: Mesa de Ayuda |
| Opciones antes mezcladas en BD_PUERTO por los tests | cmdb.bd.encrypt / CMDB_BD_ENCRYPT | Boolean | Requerida; común true, dev/test false |
| Verificación TLS explícita | cmdb.bd.trust-server-certificate / CMDB_BD_TRUSTSERVERCERTIFICATE | Boolean | Requerida; común false |

Las ocho claves legacy dejan de ser claves de entrada productiva; sus campos
Java se conservan como fachada. Para migrar un archivo, usar los nombres `cmdb.*`
y cargarlo mediante Spring. `ACTIVE_DIRECTORY` conserva su estrategia legacy,
que ya era un stub; no se implementó autenticación AD en esta tarea.

## Archivos y diferencias por perfil

Todos están bajo `src/main/resources`, sin credenciales ni destino de BD:

- `application.properties`: nombre de aplicación y valores no sensibles comunes.
- `application-dev.properties`: desactiva cifrado por defecto para SQL Server local.
- `application-test.properties`: desactiva cifrado para la instancia descartable;
  no crea una BD ni añade fixtures al JAR por seleccionar este perfil.
- `application-prod.properties`: declara cifrado obligatorio por defecto; conserva
  verificación de certificado (`trust-server-certificate=false` común).

La repetición explícita de `encrypt=true` en prod documenta su contrato y evita
que una modificación de la base común rebaje accidentalmente ese default.
El resto de propiedades no se duplica. Ningún perfil aporta credenciales,
servidor o nombre de base. Los defaults TLS pueden sobreescribirse externamente;
en la validación aislada de prod se confía expresamente en el certificado
self-signed del contenedor, sin cambiar el default distribuido.

## Selección y sobreescrituras

El mismo JAR sirve para los tres perfiles. Ejemplo con valores ilustrativos y
credenciales previamente inyectadas por el entorno, nunca incluidas en el comando:

```sh
export SPRING_PROFILES_ACTIVE=dev  # o test / prod
export CMDB_BD_SERVIDOR='<host-del-ambiente>'
export CMDB_BD_PUERTO=1433
export CMDB_BD_NOMBRE='<base-del-ambiente>'
: "${CMDB_DB_USERNAME:?Inyectar usuario del ambiente}"
: "${CMDB_DB_PASSWORD:?Inyectar secreto del ambiente}"
java -jar target/cmdb-backend-0.0.1-SNAPSHOT.jar
```

También puede seleccionarse con `--spring.profiles.active=prod`. No seleccionar
`dev,prod` o `test,prod`: la aplicación rechaza esas combinaciones.

Un archivo externo con claves Spring se añade sin sustituir las ubicaciones
estándar ni reintroducir una ruta fija de máquina:

```sh
java -Dcmdb.bd.puerto=1433 -jar target/cmdb-backend-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=test \
  --spring.config.additional-location=file:./config-externa.properties
```

Orden relevante, de menor a mayor precedencia: archivo común empaquetado,
archivo de perfil empaquetado, archivos externos de configuración (los de perfil
prevalecen sobre los comunes), entorno, propiedades Java `-D` y argumentos `--`.
`additional-location` añade ubicaciones a las estándar y sus valores pueden
sobreescribir los empaquetados. Una ruta declarada sin `optional:` debe existir.
En los tests Spring, `@DynamicPropertySource` aporta los valores generados del
contenedor por encima de fuentes ordinarias. Referencias oficiales:
[configuración externa de Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html)
y [propiedades dinámicas de tests](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/dynamic-property-sources.html).

## Pruebas y aislamiento

`CmdbBackendApplicationTests` activa explícitamente `test` y registra mediante
`@DynamicPropertySource` proveedor, host/puerto, BD y credenciales generadas por
`SqlServerTesting`, además de login, departamento y TLS. Ese helper sigue creando
un SQL Server Developer 2022 CU20 por JVM con los fixtures de CMDB-9; reutiliza el
mismo contenedor dentro de la JVM. Las pruebas legacy reciben los mismos valores
mediante `Propiedades.configurar`. No se cambian los 131 métodos ni sus aserciones.

`CMDB_TEST_CONFIG` / `-Dcmdb.test.config` siguen siendo una opción explícita
exclusiva del helper legacy de tests para su archivo de claves antiguo; no son
un mecanismo productivo ni un fallback de Spring. Sin esas opciones se usa la BD
descartable. El test de contexto Spring siempre usa el contenedor, sin heredar una
configuración dev/prod. Para la validación se usan únicamente los defaults aislados.
Los fixtures `db/legacy`, utilidades y recursos de tests siguen bajo src/test y
no deben aparecer en BOOT-INF/classes ni como dependencias del JAR productivo.

Comandos (Java 21, Maven Wrapper y Docker accesible, red si faltan descargas):

```sh
./mvnw -version
./mvnw -B clean verify
python scripts/verify-test-inventory.py
./mvnw -B dependency:build-classpath -DincludeScope=test -Dmdep.outputFile=<archivo-temporal>
# Usar el contenido de ese archivo como <classpath-resuelto>, sin credenciales:
java --class-path 'target/test-classes:target/classes:<classpath-resuelto>' scripts/ValidarPerfiles.java
```

La matriz independiente inicia otra instancia SQL Server descartable, ejecuta
el mismo JAR con dev/test/prod y utiliza un archivo temporal externo no sensible
más credenciales generadas en el entorno de cada proceso. Verifica el registro
de valores aplicados a la fachada, OPTIONS login y una lectura real de
`GET /recursos/departamento` sobre los fixtures. También comprueba precedencia
archivo → entorno → `-D` → `--`. Los casos negativos comprueban salida no cero,
mensaje de clave/perfil y ausencia de inicialización legacy. No añade métodos
al inventario de JUnit. Las aplicaciones se detienen, se eliminan archivos
temporales y Testcontainers elimina su instancia al finalizar la JVM.

## Coordinación con CMDB-13 y recuperación

CMDB-13 recibirá `cmdb.bd.usuario` y `cmdb.bd.contrasenia` como puntos sensibles
externos y la revisión de otras credenciales legacy. En CMDB-12 no se introducen
credenciales reales ni defaults secretos. No se registra la contraseña generada,
ni URLs JDBC con secretos. El archivo original de entrega se mantiene intacto;
esta migración no equivale a revisar o eliminar todos los secretos históricos.

La carpeta no tiene Git. El respaldo previo figura en
`evidence/cmdb12/backup.txt`; el diff y la lista de archivos modificados se
conservarán junto con la evidencia. No se modifican CI, endpoints ni arquitectura
de persistencia. Los errores internos legacy conocidos siguen fuera de alcance.


## Resultados de esta ejecución

Primera ejecución completa: 131 métodos, 0 fallos de aserción y 1 error en
`pruebaModificarCorrectaValidadoraExistente`. Un alta devolvió null tras una
violación UNIQUE `UK_Equipos_Codigo` (VAL-MES-6D2), y el test falló al usar ese DTO.
Los datos de pruebas usan SN aleatorios; no se modificó su generación ni ninguna
aserción para obtener otro resultado. Se conserva el log y todos los reportes
en `run1/`. La colisión no volvió a producirse al repetir la suite completa desde
una BD nueva, sin cambios de código entre ambas ejecuciones.

Segunda ejecución: **BUILD SUCCESS**, 1 min 20 s, **131 métodos esperados y
 ejecutados**, 0 fallos/errores/omitidos, sin faltantes/duplicados/inesperados.
`run2/` conserva log, reportes Surefire y cotejo. El contexto confirma explícitamente
perfil test y valores generados en la fachada. El resultado no elimina la posible
intermitencia de datos ni las excepciones internas legacy ya conocidas de CMDB-9.

| Caso de matriz | Resultado |
|---|---|
| dev | Arranque, valores efectivos, OPTIONS 200, lectura BD 200/código 4; propiedad Java prevalece sobre entorno y archivo |
| test | Arranque, valores efectivos, OPTIONS 200, lectura BD 200/código 4; entorno prevalece sobre archivo |
| prod | Arranque con cifrado, valores efectivos, OPTIONS 200, lectura BD 200/código 4; CLI prevalece sobre propiedad Java, entorno y archivo |
| sin ambiente | Salida 1, mensaje explícito, legacy sin inicializar |
| dev y test simultáneos | Salida 1, mensaje explícito, legacy sin inicializar |
| servidor obligatorio ausente | Salida 1, clave identificada, legacy sin inicializar |
| proveedor inválido | Salida 1, clave identificada, legacy sin inicializar |
| login inválido | Salida 1, clave identificada, legacy sin inicializar |
| puerto fuera de rango | Salida 1, rango identificado, legacy sin inicializar |

Cada perfil leyó cuatro departamentos del fixture mediante el código productivo.
La matriz no se limita al mensaje Started ni a valores solo presentes en archivos.
Usó un mismo JAR (hash en `artifact.json`) y un nuevo contenedor con contraseña
aleatoria. El archivo externo temporal solo contenía valores no sensibles; las
credenciales se pasaron al entorno del proceso hijo, sin registrarlas.
`matrix/results.json` conserva comandos sin secretos, valores no sensibles,
comprobaciones y estado de parada; los logs individuales están en `matrix/`.

`jar-inspection.json`: los cuatro application*.properties presentes; fixtures,
helpers y dependencias exclusivas de test ausentes; ninguna referencia productiva
a WEB-INF/conf.properties, getResourceAsStream o ServletContext.
`cleanup.json` confirma la eliminación de los tres SQL Server y los tres Ryuk
creados para las dos suites y la matriz; todas las aplicaciones de la matriz
fueron detenidas. Los avisos iniciales de prelogin mientras arranca SQL Server
y el aviso preexistente WADL/JAXB no impidieron las comprobaciones.

Archivos de implementación: `changed-files.json`; diff frente al respaldo:
`implementation.diff`. Se añadió este documento y el directorio de evidencia.
El JAR queda en `target/cmdb-backend-0.0.1-SNAPSHOT.jar`.

Registro de ejecución `CMDB12-20261101T115343Z`: 2026-11-01T11:53:43.342000+00:00 a 2026-11-01T12:04:11.178116+00:00, 628 s. Sesión Codex `01a196e5-81ec-7970-925b-7dab80c23a50`. Cuota antes/después: 58.0%/69.0% usado en cinco horas; 37.0%/39.0% semanal. Son observaciones de sesión, no consumo exclusivo. IDs de runner original/anterior no disponibles; ejecución manual asistida, sin continuación. Datos completos en `execution.json`.
