# CMDB-8 — F03-01

Los 24 recursos REST legacy y `UtilidadesServicios` residen ahora en el paquete
Java `com.atissosa.cmdb_backend.controllers`, físicamente en
`src/main/java/com/atissosa/cmdb_backend/controllers/`, bajo el paquete base
de Spring Boot. Se conserva la estructura Maven estándar. El resto de capas permanece en `src/main/java/com/atissosa/cmdb_backend`.
`docs/cmdb8-inventory.json` detalla cada traslado y las rutas y verbos originales.
No había archivos no Java asociados dentro del directorio de recursos REST.

## Integración Maven y dependencias

- Maven detecta las fuentes en `src/main/java` con su configuración estándar.
  No se utilizan raíces de fuentes adicionales ni `build-helper-maven-plugin`.
- `spring-boot-starter-jersey` usa la gestión de versiones del parent Spring Boot
  `4.1.2`: Jersey `4.0.3`, Jakarta REST API `4.0.0` y Tomcat embebido `11.0.27`.
- Se sustituyeron imports `javax.ws.rs` por `jakarta.ws.rs` y `javax.servlet`
  por `jakarta.servlet` en la integración global necesaria para compilar.
- `Aplicacion` es un `ResourceConfig` registrado como componente Spring, con
  registro explícito de los 24 endpoints y del filtro CORS. Conserva `/recursos`.
  Se corrigieron referencias obsoletas a `servicios.*` y `global.*`.
- Se actualizaron los imports de controllers usados por las clases de dominio.
- `Sistema` usaba Tomcat JDBC exclusivamente para convertir excepciones en texto.
  Ahora usa `e.printStackTrace(System.out)`, conservando la salida sin agregar una
  dependencia de pool innecesaria. No se modificó la persistencia.
- `docs/cmdb8-dependencies.txt` registra versiones presentes en el JAR final.

Las 25 clases trasladadas se compararon contra el respaldo: solo cambiaron el
paquete y el namespace Jakarta. Se preservaron métodos, lógica, rutas REST,
verbos HTTP, respuestas y el filtro CORS existente. No se añadieron jars locales.

## Validación realizada

Entorno: Java 21.0.13.0.2; Maven Wrapper 3.10.0.

```sh
./mvnw -B -o clean package -Dmaven.test.skip=true
```

Resultado: `BUILD SUCCESS`, 121 fuentes compiladas con `release 21`.
El JAR `target/cmdb-backend-0.0.1-SNAPSHOT.jar` contiene las 25 clases bajo
`BOOT-INF/classes/com/atissosa/cmdb_backend/controllers/`, sin clases bajo
el paquete suelto `controllers`.
Los tests no se modificaron, compilaron ni ejecutaron; Maven no compiló los tests. El log confirma `Not compiling test sources` y
`Tests are skipped`.

Se arrancó temporalmente el JAR en localhost con puerto libre y se consultó
`OPTIONS /recursos/login`: HTTP 200, `Allow: DELETE,POST,OPTIONS`,
`Access-Control-Allow-Origin: *`. El proceso se detuvo al finalizar.
No se invocaron operaciones de negocio ni de base de datos.

Evidencia: `docs/evidence/cmdb8/build.log`, `startup.log` y `http-options.json`.
La comprobación opcional `dependency:tree` en modo offline no se pudo ejecutar
porque el plugin no estaba en caché; se verificaron las dependencias directamente
en el JAR generado. Esto no afectó al build.

## Límites y recuperación

La configuración legacy aún referencia `WEB-INF/conf.properties`; su
externalización y los secretos corresponden a CMDB-12 y CMDB-13.
El arranque y registro HTTP no acreditan el funcionamiento contra SQL Server,
la autenticación real ni la equivalencia funcional completa. No se ejecutaron
pruebas de negocio. Persisten avisos preexistentes de APIs obsoletas y operaciones
sin comprobación de tipos en otras capas.

La carpeta no tiene repositorio Git. Respaldo previo a los cambios:
`${HOME}/Backups/cmdb8-20261101-080833/before.tar.gz`.

Cuota observada durante la ejecución (ventana de cinco horas): 11 % usado a las
11:07:34 UTC y 14 % a las 11:09:31 UTC; semanal 30 % usado en ambas lecturas.
Estos valores corresponden a observaciones del historial local, no a una medición
exacta del consumo exclusivo de esta tarea.

## Corrección de estructura Spring Boot

Se corrigió la interpretación anterior de raíz del proyecto: controllers es un
subpaquete del paquete base com.atissosa.cmdb_backend, junto a domain, dto, logic
y persistency. Se actualizaron imports y registro Jersey, se eliminó la carpeta
controllers en la raíz física y la configuración adicional de Maven. Las
dependencias modernas se conservan. La evidencia de build se regeneró tras
esta corrección.
