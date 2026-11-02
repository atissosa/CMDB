# CMDB-15 — F10-01: contrato de rutas y verbos

## Corrección de exposición

El frontend real de la entrega, en
`Desktop/CMDB_ENTREGA/Fuentes/cmdb-frontend/public_html/conf/propiedades.js`,
usa `http://localhost:8080/cmdb-backend/recursos/`. Esto difiere de la plantilla
vacía de `Aplicacion/propiedades.js` consultada en tareas anteriores. La entrega
fuente concreta permite resolver la compatibilidad, sin modificar el cliente.

Se añadió únicamente esta propiedad de exposición productiva en la configuración
común de los perfiles:

```properties
server.servlet.context-path=/cmdb-backend
```

La URL base efectiva predeterminada pasa a ser
**http://localhost:8080/cmdb-backend/recursos/**. `/cmdb-backend` es el
contexto externo que correspondía al WAR legacy; `/recursos` sigue siendo el
prefijo propio de Jersey de `@ApplicationPath`. Los 24 recursos conservan sus
anotaciones, mayúsculas/minúsculas, parámetros y verbos. No se añaden aliases,
redirecciones ni nuevos filtros. La configuración de host/puerto permanece externa;
las validaciones sustituyen solo la autoridad por 127.0.0.1 y un puerto aleatorio,
para no ocupar servicios existentes ni usar un despliegue real.

La propiedad estándar es sobreescribible mediante
`SERVER_SERVLET_CONTEXT_PATH`, `-Dserver.servlet.context-path` o un argumento
`--server.servlet.context-path`. Un operador puede elegir otro contexto en otro
despliegue, pero el default preserva el frontend inventariado. No existe un alias
simultáneo en `/recursos` desde la raíz. Los scripts de validación anteriores se
ajustaron al contexto actual; la evidencia de sus ejecuciones pasadas no se alteró.
[Referencia oficial de la propiedad](https://docs.spring.io/spring-boot/appendix/application-properties/).

## Matriz y fuentes

`docs/cmdb15-contract-matrix.json` y su fixture de pruebas conservan:

- Las **51 operaciones públicas**, con clase/método, ruta legacy y migrada completas,
  verbo, parámetros y sus anotaciones, JSON, delegación y líneas del frontend.
- **39 sitios de llamada** reales a hacerRequest en funciones.js, expandidos a
  **57 variantes** por recursos dinámicos. Son **49 pares ruta/verbo distintos**;
  las otras 2 operaciones públicas se mantienen aunque ese cliente no las invoque.
- El registro legacy `src/java/global/Aplicacion.java`, las anotaciones de los
  recursos y el web.xml sin un prefijo adicional propio de Servlet.
- La política de hacerRequest: método convertido a mayúsculas, GET con datos de
  consulta, otros métodos con JSON cuando hay datos, Content-Type application/json
  y Authorization a partir de credenciales. No se eliminan cuerpos en DELETE.

Las variantes dinámicas son pc/impresora/validadora/otroEquipo y
plantillaPC/plantillaIMPRESORA/plantillaVALIDADORA/plantillaOTROEQUIPO, según los
switches y normalización de tipoEquipo del cliente. La extracción no ejecuta la
interfaz gráfica ni evalúa datos de usuarios; conserva expresiones y líneas
originales sin valores operativos. `inventory-source.py` documenta el procedimiento.
Los hashes de las fuentes de referencia permiten verificar que no se modificaron.
Los datos procedentes del DOM se sustituyen por valores sintéticos controlados;
las claves GET se extraen de sus objetos de parámetros, incluyendo id, numeroSerie,
marca y tipo según cada rama. No se afirma haber ejecutado un navegador ni el
flujo visual de la interfaz. Las dos operaciones sin llamada de ese cliente,
GET equipo y GET lugar, siguen incluidas en la matriz pública completa.

`source-comparison.json` vuelve a cotejar los 24 controllers y UtilidadesServicios:
los 25 cuerpos siguen iguales a la referencia, ignorando package/imports, espacios
y comentarios. `exposure-change.json` separa contexto anterior vacío, contexto
restaurado y prefijo Jersey; el estado anterior se deriva de la configuración y
los resultados guardados en CMDB-14, no de una ejecución adicional del servidor viejo.

## Pruebas HTTP de contrato

Cinco métodos nuevos de ContratoRutasRestTests usan Spring Boot RANDOM_PORT,
perfil test y SQL Server Developer 2022 CU20 descartable, con secretos canónicos
generados programáticamente según CMDB-13:

| Método | Cobertura |
|---|---|
| mantieneLas51OperacionesEnLaUrlCompleta | Las 51 operaciones sobre la ruta pública completa, con JSON cuando corresponde, sin 404/405 de mapping ni redirecciones |
| conservaLasLlamadasInventariadasDelFrontend | 57 variantes asociadas a las 39 líneas reales del cliente, preservando ruta y verbo |
| contrastaParametrosAusentesVaciosYCodificados | 45 casos: 15 parámetros, cada uno ausente, vacío y con valor codificado; enteros y cadenas con á, espacio, &, +, /, ? y # |
| rechazaRutasYVerbosNoSoportados | 61 casos: PATCH no soportado → 405; segmentos extra, rutas inexistentes y cambios de capitalización relevantes → 404, sin redirección |
| conservaDeleteConCuerpoYCabeceraDelFrontend | Flujo real de login/POST/GET/PUT/GET/DELETE con departamento sintético; escritura confirmada en BD y Basic de solo usuario como lo usa el frontend fuera del login |

La referencia de resultado no acepta un 500 arbitrario como mapping correcto.
ContratoRutasHttpTesting invoca directamente el controller que conserva la
delegación legacy, con query params tipados y el equivalente del DTO vacío {}, y
compara **HTTP y código de Respuesta** contra la petición HTTP. Se comprueban
además las anotaciones de ruta y verbo y se rechaza cualquier Location/3xx.
Los errores funcionales esperados quedan separados de un 404/405 de mapping en
cada resultado. El logout void conserva 204 sin cuerpo. Las entidades vacías se
usan para recorrer rutas sin ejecutar altas/bajas sobre datos originales; el
flujo positivo independiente comprueba que DELETE sí recibe y usa su JSON.

Esta referencia no arranca el WAR antiguo ni compara todos los payloads serializados:
se apoya en los cuerpos/delegaciones conservados y los contratos de anotaciones y
cliente. La exhaustividad de campos y códigos de negocio es CMDB-16. No se
cambian reglas, DTOs, mensajes, autenticación, CORS o persistencia para satisfacer
los casos. El cliente original permanece intacto.

## Inventario y comandos

Los **131 métodos base** y los **6 de CMDB-14** siguen presentes. Los cinco nuevos
se añaden de forma trazable a test-inventory-additions.json: **142 esperados**.
El verificador sigue detectando métodos faltantes, duplicados, inesperados,
omisiones y fallos/errores; no se introducen filtros ni exclusiones.

```sh
./mvnw -version
./mvnw -B clean verify
python scripts/verify-test-inventory.py
./mvnw -B dependency:build-classpath -DincludeScope=test -Dmdep.outputFile=<archivo-temporal>
java --class-path 'target/test-classes:target/classes:<classpath-resuelto>' scripts/ValidarContratoRutas.java
```

Java 21.0.13.0.2 / Maven Wrapper 3.10.0. Se requiere Docker accesible y red si
faltan imágenes/dependencias. El harness inicia otra BD descartable y el mismo
JAR con perfil test, localhost y puerto libre; inyecta credenciales generadas
solo en el entorno del hijo, no en argumentos o archivos. Repite la matriz completa
y la escritura con cuerpo y cabecera del cliente. Las cuentas y departamentos
adicionales son sintéticos y desaparecen con el contenedor; las sesiones y la
aplicación se detienen. No se accede a sistemas externos ni se modifica CI.

## Primer contraste y resultado de la suite

Primera ejecución completa: **142 métodos**, un fallo en el comparador de un
parámetro vacío de impresora. La referencia directa había pasado el literal
internado ""; la lógica legacy compara algunas cadenas con `!= ""`, mientras el
valor decodificado por HTTP tiene otra identidad. Se corrigió únicamente la
construcción de las cadenas del oráculo para representar ese valor recibido.
No se cambió la condición de negocio ni se relajaron las comprobaciones de
HTTP/código. `run1/` conserva fallo, reportes y resultados que ya habían pasado.

Repetición completa con una BD nueva: **BUILD SUCCESS**, 1 min 49 s,
**142/142**, 0 fallos/errores/omitidos, sin faltantes/duplicados/inesperados.
`run2/` conserva esa repetición y su inventario preliminar de 48 variantes del cliente.
La revisión final de los flujos del DOM añadió las variantes de pc/impresora/
validadora en los tres puntos que seleccionan recurso dinámicamente, además de
las claves GET de los objetos de datos. La matriz final contiene 39 sitios,
57 variantes y 49 operaciones distintas del cliente. Se ejecuta otra suite
completa y el harness del JAR con esta cobertura final; no se omiten casos.
Los resultados finales se conservan aparte de los preliminares. Las seis pruebas de CMDB-14 también
siguen pasando con el contexto corregido.

Las comparaciones por identidad de cadenas y excepciones internas legacy siguen
siendo límites existentes; una suite verde no demuestra que sus reglas sean
correctas ni que sus utilidades sin aserciones no registren errores. No se amplía
CMDB-15 para repararlas. El aviso opcional WADL/JAXB tampoco se confunde con un
fallo de proveedor de las operaciones JSON que sí se comprobaron.


## Validación final de cobertura completa

Tercer `clean verify`, después de ampliar las selecciones del DOM y los objetos
GET del cliente: **BUILD SUCCESS**, 1 min 35 s, **142/142**, 0 fallos/errores/omitidos,
sin faltantes, duplicados o inesperados. El inventario sigue siendo 131 base + 6
CMDB-14 + 5 CMDB-15. Reportes definitivos y resultados por petición en `run3/`.
No se sustituyen los reportes anteriores; los preliminares quedan identificados.

El JAR final pasó nuevamente **51 operaciones, 57 variantes de frontend, 45
casos de parámetros y 61 negativos**, más el flujo positivo de escritura y DELETE
con cuerpo y cabecera de solo usuario. No hubo 404/405 de mapping en las rutas
válidas ni redirecciones. Las respuestas funcionales se contrastan con la referencia
y no se confunden con inexistencia de mapping. La contraseña generada no aparece
en la salida del hijo y la aplicación se detuvo.

`jar-http/results.json` y `jar-http/startup.log` son definitivos;
`jar-http-preliminary/` pertenece al primer inventario. `artifact.json` registra
el SHA-256 y el contexto empaquetado; el JAR contiene 24 recursos únicos y excluye
fixtures/helpers/dependencias de pruebas. `cleanup.json` verifica eliminación de
los diez contenedores de las tres suites y los dos harness: cinco SQL Server y
cinco Ryuk. `reference-files-unchanged.json` confirma que las referencias originales
no se modificaron. El diff afecta código productivo solo en application.properties;
los controllers, lógica, DTOs, persistencia y POM permanecen sin cambios.

Registro `CMDB15-20261101T234130Z`: 2026-11-01T23:41:30.864000+00:00 a 2026-11-02T00:04:56.189563+00:00, 1405 s. Sesión `01a196e5-81ec-7970-925b-7dab80c23a50`. Estimación vigente M/30 minutos, reserva orientativa 8,5 millones de tokens según Plane; no hay medición exclusiva de tokens. Cuota antes/después: 42.0%/61.0% usado en ventana de cinco horas, 50.0%/53.0% semanal. Observaciones de sesión, no consumo exclusivo de CMDB-15.
