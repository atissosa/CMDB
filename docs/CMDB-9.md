# CMDB-9 — F04-01: migración de pruebas legacy

Origen: `${HOME}/Desktop/CMDB_ENTREGA/Fuentes/cmdb-backend/test`.
Destino: `src/test/java/`, conservando paquetes legacy (`Equipo`, `Lugar`,
`Usuario`, `conjuntoDePruebas`, `utilidades`). Las pruebas nuevas de Spring Boot
conservan su ubicación en `src/test/java/com/atissosa/cmdb_backend/`.

## Migración

Se copiaron 33 clases legacy con 130 métodos `@Test`; no se borró la entrega
original. El inventario `docs/cmdb9-inventory.json` conserva origen, destino,
nombre de clase, paquete, métodos de prueba y hash de cada archivo original.
Se preserva también el test `contextLoads` existente: 131 métodos esperados.

Las 29 clases legacy que contienen métodos `@Test` recibieron el sufijo `Test`
para el descubrimiento estándar de Surefire. La suite agregadora conserva su
nombre y referencias actualizadas: no se descubre automáticamente junto a las
clases individuales, evitando ejecutar sus casos dos veces. Ningún método de
prueba fue eliminado, deshabilitado o sustituido. Los cuerpos de las pruebas
funcionales solo cambiaron por imports de producción y nombres de clase.

Las cuatro utilidades de testing permanecen bajo `src/test/java/utilidades/`.
`Lugar/text.txt` (vacío en el origen) se conservó en `src/test/resources/Lugar/`.

- Los imports `dto`, `logica`, `errores`, etc. apuntan ahora a sus paquetes
  productivos `com.atissosa.cmdb_backend.*`.
- `junit:junit`, `junit-vintage-engine`, `mssql-jdbc` y
  `testcontainers-mssqlserver` tienen `scope=test`, con versiones gestionadas por
  el parent Spring Boot 4.1.2. No se convirtieron anotaciones ni aserciones de
  JUnit 4 a Jupiter; eso corresponde a CMDB-21.
- Las rutas `C:/pruebas` y `c:/informe` pasan a
  `target/legacy-test-output/` y `target/legacy-test-report/`.
- `EntornoTesting` ya no requiere el directorio Windows del autor original.
- No se modificaron fuentes ni recursos productivos: se compararon con el
  respaldo anterior a la tarea.

## SQL Server aislado y reproducible

Sin configuración externa, `utilidades.SqlServerTesting` inicia mediante
Testcontainers una instancia SQL Server Developer 2022
`mcr.microsoft.com/mssql/server:2022-CU20-ubuntu-22.04`, con puerto aleatorio,
contraseña generada y datos descartables. Se acepta la licencia de la imagen
para este uso de desarrollo/pruebas mediante `acceptLicense()`. Se limita el
buffer del motor a 2048 MB. Requiere Docker accesible y conectividad para la
primera descarga de imágenes.

Cada JVM Maven recibe un contenedor nuevo. Los scripts de la entrega están en
`src/test/resources/db/legacy/`; el script de datos fue convertido de UTF-16 a
UTF-8 y el DROP inicial se hizo condicional para permitir la inicialización de
una instancia vacía. Solo se ejecutan contra el contenedor creado por este
helper. `03_precondiciones_tests.sql` añade los tres registros estáticos
que los casos de alta duplicada esperan encontrar: departamento
`Nombre del depatamento`, service `Nombre del service` y PC `MXL7242PC1`.
Esos registros no estaban en el dump original. Las pruebas y sus aserciones
se mantienen sin cambios; se completan sus precondiciones de datos. El departamento por defecto es `Mesa de Ayuda`, como en la configuración
legacy. El contenedor se elimina al finalizar la JVM y mediante el recolector de
Testcontainers. No se utiliza ninguna base externa por defecto.

La suite legacy modifica datos e incluye un método que reformatea códigos de
los equipos existentes. Se ejecuta contra esta copia aislada de los datos de
la entrega. No usar la configuración externa contra una BD de producción.

Opcionalmente se puede indicar `CMDB_TEST_CONFIG` o `-Dcmdb.test.config` con
un archivo externo de propiedades de una BD exclusiva de pruebas. Hay una
plantilla sin credenciales reales en
`src/test/resources/cmdb-test.example.properties`. Esa opción no reinicializa
ni crea una base externa automáticamente.

## Ejecución

```sh
./mvnw -version
./mvnw -B clean test
python scripts/verify-test-inventory.py
```

No se requieren perfiles, filtros `-Dtest` o configuración especial del IDE.
La suite debe ejecutarse sin `skipTests` ni `maven.test.skip`. Los reportes de
cada ejecución aparecen en `target/surefire-reports/`.

La compilación de las pruebas pasó con Java 21 y Maven 3.10.0. Se conservan
los resultados completos y su cotejo con el inventario en
`docs/evidence/cmdb9/` al finalizar la validación. Un resultado de compilación
exitoso no implica que la suite haya pasado.

## Recuperación

Respaldo anterior a la tarea:
`${HOME}/Backups/cmdb9-20261101-082338/before.tar.gz`.
La carpeta del proyecto no tiene repositorio Git.

## Primera ejecución completa y ajustes de fixtures

La primera ejecución completa detectó 131 métodos de test y tres fallos de
finalización de clase adicionales (Surefire informa 134 tests, 2 fallos y
3 errores). Los tres casos de entidad existente recibieron alta exitosa
en lugar del código de duplicado esperado porque faltaban sus registros
en el dump. La comparación de archivos en @AfterClass hizo visibles también
los casos que no tienen assert directo. Se añadieron las precondiciones de
datos en el script 03 y se repitió la suite desde una base vacía.

La rutina de mantenimiento `FormatearCodigosDeEquiposEnBDTest` no contiene
aserciones propias y el código legacy puede registrar excepciones de datos
al recorrer equipos históricos. Un informe de JUnit exitoso no garantiza
que esa rutina esté libre de errores internos; no se modificó su lógica.

## Resultado con fixtures completos

Primera ejecución con las precondiciones completas: `./mvnw -B clean test`,
`BUILD SUCCESS`, 131 pruebas, 0 fallos, 0 errores y 0 omitidas (1 min 16 s).
`python scripts/verify-test-inventory.py` confirmó los 131 métodos esperados,
sin métodos faltantes, duplicados o inesperados. Evidencia en
`docs/evidence/cmdb9/run2/`. La ejecución fallida anterior se conserva en
`docs/evidence/cmdb9/run1/` para explicar el ajuste de datos.

Referencias para la compatibilidad temporal y el entorno aislado:
- https://docs.junit.org/6.1.3/overview.html (JUnit Vintage para pruebas JUnit 4).
- https://java.testcontainers.org/modules/databases/mssqlserver/ (SQL Server descartable).
- https://maven.apache.org/surefire/maven-surefire-plugin/test-mojo.html (descubrimiento estándar).

Segunda ejecución consecutiva con los mismos fixtures: `./mvnw -B clean test`,
`BUILD SUCCESS`, 131 pruebas, 0 fallos, 0 errores y 0 omitidas (1 min 21 s).
El cotejo volvió a confirmar 131 métodos, sin faltantes ni duplicados.
Evidencia en `docs/evidence/cmdb9/run3/`.

No se aplicaron filtros ni exclusiones y las dos ejecuciones usaron contenedores
y bases nuevos. Los archivos de evidencia de ejecución y observaciones de cuota
están en `docs/evidence/cmdb9/execution.json`. La cuota observada es la de la
sesión y no una medición exacta de consumo exclusivo de la tarea.
