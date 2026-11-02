# CMDB-16 — F11-01: compatibilidad de payloads y respuestas

La aplicación conserva el contrato del frontend de la entrega en
`/cmdb-backend/recursos/`, usando el proveedor Jersey/Jackson elegido para la
migración. Se registró un `ContextResolver<ObjectMapper>` para omitir referencias
nulas, mantener listas vacías y valores primitivos, respetar los nombres JavaBeans
originales (incluidas las iniciales mayúsculas), ignorar campos desconocidos y
rechazar nulos explícitos en propiedades primitivas. El mixin de `DTOFiltroEquipo`
conserva el rechazo de `esPrestamo: null` observado en JSON-B, aunque su setter
recibe un Boolean y el getter devuelve boolean.

No cambiaron DTOs, controladores de negocio, permisos, frontend, dependencias ni
los mensajes y códigos de `Respuesta`. Se mantiene un único proveedor JSON.
Los errores de lectura JSON reciben HTTP 400 con `text/plain; charset=UTF-8` y el
texto fijo `JSON inválido.`. Los mappers predeterminados de Jackson se desactivan
para evitar respuestas con clases internas o valores enviados por el cliente.
Los errores de negocio conservan sus códigos y estados HTTP originales.

## Contrato inventariado

[La matriz](cmdb16-payload-matrix.json) enumera las 22 clases de datos, sus
propiedades JSON exactas, tipos de getters, herencia, tratamiento de nulos, fechas,
y las 51 operaciones con parámetros, delegación y consumidores del frontend.
Los nombres de las propiedades se contrastan con las salidas del serializador
original congeladas en los fixtures. Los números siguen siendo números y los
booleanos siguen siendo booleanos; se conserva la coerción de números escritos
como strings que utiliza el frontend.

Casos que requieren conservar su forma exacta:

- `DTOPlantillaPc.tamanoñoDisco` incluye una ñ y así lo consume el frontend.
- `DTOParametroMantenimientoPrestamo` recibe las seis claves con iniciales
  `RCant`, `ACant` y `VCant`. GET `prestamo` devuelve un array de seis enteros,
  en el orden original, que el frontend lee por índice.
- Las fechas expuestas en equipos e histórico son strings, respectivamente
  `yyyy-MM-dd` y `dd/MM/yyyy HH:mm:ss.SSS`; no se convirtieron a timestamps.
- `DTOIncidente` y `DTOReparacion` contienen Date pero no están referenciados por
  endpoints activos. Se inventarían sin declarar una nueva API ni garantizar un
  formato de fecha de una ruta inexistente. `DTOPlantillaEquipo` es abstracta y
  queda cubierta mediante sus cuatro clases concretas.
- Se conserva el comportamiento heredado de los setters de `DTOFiltroEquipo`,
  incluido el setter de id que asigna idLugar y el rechazo de determinadas listas
  nulas. Su serialización vacía falla en el original por un getter que desempaqueta
  un Boolean nulo; ese caso no se presenta como un payload válido ni se corrige
  aquí modificando negocio.

`Respuesta` mantiene los campos `codigo`, `tipo`, `mensaje` y `retorno`. Una
referencia retorno nula se omite y una lista vacía aparece como `[]`. Se prueban
los 30 códigos originales con ambos retornos, incluidos sus estados 200/500/401/403.
Los catálogos conservan los valores y el orden de los enums originales.

## Referencia independiente y pruebas

Se recompilaron temporalmente los fuentes originales de
`Desktop/CMDB_ENTREGA/Fuentes/cmdb-backend` con Java 21 y sus bibliotecas
JSON-B/Yasson 1.0. Los binarios entregados requieren una versión de Java posterior;
no se modificaron. Para compilar una referencia antigua a Tomcat JDBC se descargó
el artefacto oficial 8.5.100 únicamente al directorio temporal, con checksum
verificado; no se añadió al proyecto ni al JAR. La generación no abre conexiones
a bases originales. Los datos de referencia son sintéticos.

Los tres fixtures en `src/test/resources/http/cmdb16/` se generaron con ese
serializador original, antes de compararlos con Jackson. Los generadores rechazan
sobrescribir un fixture existente. Sus fuentes, hashes y procedencia están en
`docs/evidence/cmdb16/`. Se comparan árboles JSON: el orden de propiedades es
irrelevante, pero los nombres, tipos, omisiones, listas y valores deben coincidir.
No se arrancó un WAR histórico completo: la referencia independiente es su código
y serializador; el transporte HTTP se valida en la aplicación migrada.

Se añadieron seis métodos de prueba HTTP:

- 54 variantes soportadas de salida de DTOs y Mensaje.
- 93 ejemplos de entrada: completos, omitidos, nulos explícitos, números entre
  comillas, campos desconocidos y listas individualmente nulas; 82 aceptados y
  11 rechazados, según el original.
- 60 respuestas de los 30 códigos de negocio con retorno nulo o lista vacía.
- Endpoints reales: alta, lectura y baja con Unicode, validación 500, autenticación
  401, autorización 403, ausencia de resultados y cuatro catálogos de enums.
- Mantenimiento: nombres capitalizados, números enviados como strings, retorno
  de seis números y restauración de parámetros mediante el DTO original.
- Tres entradas malformadas en el endpoint real de PC, sin exposición de sus
  valores o de clases internas en la respuesta.

Los endpoints auxiliares de fixtures existen solamente en el contexto de pruebas.
La validación del JAR ejecutable usa `scripts/ValidarPayloads.java`, SQL Server
2022 descartable y endpoints productivos, y comprueba que la ruta auxiliar devuelve
404. Las credenciales de base y las cuentas de aplicación se generan localmente;
no se usan credenciales históricas ni servicios de AWS.

## Ejecución y evidencia

`./mvnw -B clean verify` debe conservar los 131 métodos originales y los 11 de
CMDB-14/15, además de los seis nuevos: 148 métodos. El inventario se comprueba con
`scripts/verify-test-inventory.py` y `docs/test-inventory-additions.json`.

Se conservan las ejecuciones fallidas sin alterar resultados: `run1` detectó una
expectativa incorrecta de mi prueba de mantenimiento (objeto en lugar del array
original), y `run2` detectó el fixture de catálogos ausente por un error de
compilación de su generador. Se corrigieron las pruebas con la referencia original;
no se cambiaron DTOs ni lógica para acomodar esos fallos. Los logs también incluyen
excepciones heredadas de escenarios negativos; el resultado se determina mediante
las aserciones y el inventario, no por ausencia de mensajes en consola.

La evidencia final incluye resultados de Maven, pruebas HTTP, ejecución del JAR,
inspección del empaquetado, integridad de fixtures, cambios respecto al respaldo y
limpieza de contenedores. El respaldo anterior a la tarea figura en
`docs/evidence/cmdb16/backup.txt`. No hay repositorio Git en esta entrega: se aporta
un diff y la referencia al respaldo para revisión y reversión.

Resultado final: **run3 BUILD SUCCESS, 148 pruebas sin fallos, errores ni omisiones**;
inventario completo, JAR HTTP aprobado, integridad de los tres fixtures y empaquetado
verificados, aplicación detenida y ocho contenedores eliminados. Véanse
`docs/evidence/cmdb16/run3/inventory.txt`, `jar-http/results.json`,
`artifact-checks.json` y `cleanup.json`.
