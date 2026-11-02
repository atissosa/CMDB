# CMDB-23 — revisión de credenciales históricas

Se completó una revisión local de la copia activa, la entrega original y los 5
respaldos CMDB disponibles. No se revelan valores, no se probaron credenciales y
no se accedió a AWS ni a ninguna base externa. Los originales y respaldos no se
modificaron. Solo se añadieron este informe y evidencias sin secretos.

El usuario confirmó que la entrega corresponde a una instalación real en AWS,
pero que el servicio fue dado de baja y la aplicación ya no está allí. Es una
declaración del responsable, no una verificación independiente en la cuenta AWS.
La exposición en archivos históricos sí está confirmada; la vigencia/reutilización
de las credenciales y el estado de recursos que pudieran conservarse no se
comprobaron. No hay evidencia suficiente para afirmar un acceso activo a AWS.

## Hallazgos confirmados

| Hallazgo | Ubicación | Evidencia y estado |
|---|---|---|
| Usuario y contraseña de conexión SQL Server en texto claro | Desktop/CMDB_ENTREGA/Fuentes/cmdb-backend/web/WEB-INF/conf.properties, líneas 6 y 7 | Claves BD_USUARIO / BD_CONTRASENIA no vacías; un par de credenciales histórico |
| Copia del mismo par de conexión | Desktop/CMDB_ENTREGA/Fuentes/cmdb-backend/build/web/WEB-INF/conf.properties, líneas 6 y 7 | Igualdad de valores comprobada en memoria, sin registrarlos |
| Credencial incluida en un artefacto distribuible | Desktop/CMDB_ENTREGA/Fuentes/cmdb-backend/dist/cmdb-backend.war → WEB-INF/conf.properties, líneas 6 y 7 | El mismo par queda dentro del WAR; no basta con sanear el archivo fuente |
| Ocho cuentas de aplicación con contraseñas en texto claro | Desktop/CMDB_ENTREGA/BD/02_datos.sql, líneas 1693 a 1700, IDs 3 a 10 | En las ocho filas la contraseña es idéntica al nombre de usuario. No se publican ninguno de los dos valores |
| Copias de esas ocho cuentas en respaldos locales | Backups/cmdb12-20261101-085519/before.tar.gz y Backups/cmdb13-20261101-091037/before.tar.gz | Miembro src/test/resources/db/legacy/02_datos.sql, mismas filas importadas |

Las rutas anteriores se expresan relativas a `${HOME}`. El detalle con líneas,
IDs de fila y comparación de grupos está en `evidence/cmdb23/findings.json`.
Se trata de un par de conexión repetido en tres ubicaciones, más ocho pares de
usuarios de aplicación; no son tres cuentas SQL distintas.

El par de conexión es una credencial SQL Server, no una clave IAM de AWS. El host
configurado es un nombre de servidor sin identificación AWS explícita: el origen
de la entrega no demuestra por sí solo que ese archivo sea la configuración
productiva que se utilizó en AWS. No se resolvió el host ni se intentó acceder.

## Copia activa y falsos positivos descartados

- En cmdb-backend, las ocho identidades importadas fueron sustituidas por datos
  sintéticos en CMDB-13. El cotejo de los valores históricos contra el texto activo,
  incluidos documentos, evidencias y archivos de target legibles, no encontró sus
  valores originales. El fixture fuente y su copia de target/test-classes contienen
  únicamente las diez identidades de prueba: dos sintéticas originales y ocho
  sustituidas; no se interpretan como diez credenciales productivas pendientes.
- La configuración común del JAR activo usa los placeholders canónicos de entorno;
  no se encontró un par BD_USUARIO/BD_CONTRASENIA literal en la copia activa ni en
  sus archivos de configuración empaquetados revisados.
- Aplicacion/conf.properties de la entrega tiene ambos campos de credenciales
  vacíos. Su WAR Aplicacion/cmdb-backend.war también los tiene vacíos. No se
  confunden con el WAR de Fuentes/cmdb-backend/dist, que sí contiene valores.
- No se encontraron claves de acceso AWS, nombres de variables de credenciales AWS,
  tokens GitHub/JWT/Google ni claves privadas según los patrones revisados. Es un
  resultado limitado al alcance y patrones, no una certificación absoluta.
- Las ocho constantes DATA_API_KEY de Bootstrap son namespaces de eventos, no
  credenciales de una API. Se descartaron referencias Ant a variables, un token de
  reemplazo de build y marcadores sintéticos generados en runtime. No se registraron
  los literales candidatos en los informes.

## Plan de disposición y vigencia

| Acción | Responsable | Situación |
|---|---|---|
| Confirmar baja de la aplicación | Usuario/responsable de la instalación | Confirmado por el usuario; sin comprobación remota |
| Confirmar si estas contraseñas se reutilizaron en otros servicios aún activos | Responsable de las cuentas originales | Pendiente; no se puede deducir de los archivos |
| Si hubo reutilización, reemplazar la credencial en el sistema que siga activo | Responsable autorizado de ese sistema | Condicional; no ejecutado en esta revisión |
| Decidir conservación privada, saneamiento o eliminación de entregas y respaldos históricos | Propietario de las copias | Pendiente; no se alteraron ni eliminaron archivos |
| Evitar distribuir de nuevo el WAR y SQL históricos con valores originales | Propietario de la entrega | Usar la copia activa saneada o preparar una copia histórica saneada dentro de un alcance autorizado |

La baja del servicio reduce el riesgo de uso directo sobre aquella aplicación,
pero no demuestra ausencia de reutilización ni elimina las copias legibles. No es
necesario volver a poner en marcha el servicio para identificar esta exposición.
La recomendación condicional sobre reutilización sigue la orientación oficial de
[Microsoft sobre contraseñas que no se usen en otras cuentas](https://github.com/MicrosoftDocs/sql-docs/blob/live/docs/relational-databases/security/secure-sql-server.md).
No se prescribe una rotación sobre recursos que ya no existen ni se afirma una
filtración pública o acceso no autorizado: se confirmó presencia de secretos en
archivos, no el alcance de su distribución.

CMDB-23 quedó **Done**, por conformidad del usuario tras revisar el informe y
confirmar la baja de la instalación. Se cierra la evaluación documentada del
seguimiento. La reutilización sigue sin verificarse y las copias históricas se
conservan; no se declara ejecutada una rotación, revocación o eliminación.

## Evidencia y límites

- `findings.json`: inventario por ámbito, ubicaciones, líneas e IDs, sin valores.
- `additional-review.json`: patrones, candidatos clasificados y comparación de WARs.
- `context.json`: declaración del usuario y límites de verificación.
- `checks.json`: conteos y comprobaciones estáticas.
- `audit.py`: procedimiento local reproducible; solo emite metadatos sin secretos.

Se revisaron archivos legibles de la entrega y copia activa, configuración y SQL
dentro de WAR/JAR propios y miembros .properties/.sql de los 5 tar de respaldo.
No se inspeccionaron secretos en procesos, cuentas cloud, discos remotos ni
repositorios externos. La revisión de binarios se limitó a esos miembros propios;
no se afirma haber examinado cada byte de dependencias de terceros o formatos
arbitrarios. No se ejecutó Maven ni SQL: no hubo cambios de código ni se intentó
usar credenciales. Los valores se compararon solo en memoria, sin volcarlos ni
registrar hashes individuales de contraseñas.

Registro `CMDB23-20261101T225241Z`: 2026-11-01T22:52:41.702000+00:00 a 2026-11-01T22:59:19.265312+00:00, 398 s. Sesión `01a196e5-81ec-7970-925b-7dab80c23a50`. Estimación original no indicada en la tarea. Cuota antes/después: 18.0%/24.0% usado en ventana de cinco horas, 46.0%/47.0% semanal. Son observaciones de sesión, no consumo exclusivo. No hubo cambios de código ni pruebas de credenciales.

Cierre confirmado en Plane: 2026-11-01T23:17:18.632288Z. Comentario de cierre: `58ca61ae-8bad-469b-bd05-65a2e96b9cd7`, publicado y verificado antes de aplicar Done. Evidencia: `evidence/cmdb23/plane-closure.json`.
