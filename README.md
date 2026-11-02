# CMDB Backend

Backend CMDB con Java 21, Spring Boot, Jersey y Microsoft SQL Server.
Esta guía permite ejecutarlo localmente con la base persistente de desarrollo.

## Requisitos

- JDK 21 y `java` disponible en el PATH.
- Python 3.
- Docker Engine en ejecución, con acceso al daemon y Docker Compose.
- Acceso a Internet en la primera preparación para descargar dependencias e imagen.
- Memoria disponible para SQL Server, configurado con un límite de 2048 MB,
  además del backend y los otros servicios de la máquina.

El proyecto incluye Maven Wrapper; no hace falta instalar Maven por separado.
Podés comprobar las herramientas con:

```bash
java -version
python3 --version
docker info
docker compose version
```

El entorno utiliza SQL Server **Developer**, para desarrollo y pruebas. Su
configuración incluye la aceptación de la licencia de Microsoft (`ACCEPT_EULA=Y`).
Ver [requisitos y licencia](docs/CMDB-24.md#requisitos-y-licencia).

## Primera ejecución

Abrí una terminal en el proyecto:

```bash
cd ${HOME}/Projects/CMDB/cmdb-backend

# Generar configuración privada y contraseñas locales.
python3 scripts/dev/cmdb-dev.py configure

# Iniciar SQL Server e inicializar cmdb_dev si todavía no existe.
python3 scripts/dev/cmdb-dev.py up

# Compilar el JAR y ejecutar la suite completa.
./mvnw -B clean verify

# Iniciar el backend en primer plano.
python3 scripts/dev/cmdb-dev.py backend
```

Esperá a que aparezca `Started CmdbBackendApplication` en la terminal.
El lanzador selecciona el perfil `dev` y carga las credenciales externas de la
configuración privada. No necesitás escribir contraseñas en el comando.

La suite de compilación necesita Docker: utiliza su propio SQL Server descartable,
separado de `cmdb_dev`.

## Ejecuciones posteriores

Si el JAR ya está construido y no cambió el código:

```bash
cd ${HOME}/Projects/CMDB/cmdb-backend
python3 scripts/dev/cmdb-dev.py up
python3 scripts/dev/cmdb-dev.py backend
```

`up` conserva los datos existentes; no vuelve a cargar el seed ni borra la base.
Si modificaste código Java o configuración incluida en el JAR, reconstruí con
`./mvnw -B clean verify` antes de iniciar el backend.

## URL y comprobación

| Servicio | Dirección predeterminada |
| --- | --- |
| API del backend | `http://localhost:8081/cmdb-backend/recursos/` |
| SQL Server | `127.0.0.1:14330` |
| Base de desarrollo | `cmdb_dev` |

Desde otra terminal, comprobá una lectura real de la API:

```bash
curl --fail-with-body -i \
  http://localhost:8081/cmdb-backend/recursos/departamento
```

La respuesta esperada es HTTP 200 y un JSON con `codigo: 4` y `retorno` con los
departamentos disponibles. En la base inicial incluye `Mesa de Ayuda`.
Usá una ruta concreta como la del ejemplo; la URL base no es una página web.

Para probar desde el frontend, configurá su URL del backend como:

```javascript
recursosBackend: "http://localhost:8081/cmdb-backend/recursos/"
```

En esta máquina el puerto 8080 está reservado para Plane; por eso el lanzador de
desarrollo utiliza 8081. Esta guía no modifica la configuración del frontend.

## Usuario y contraseñas

La cuenta funcional inicial para ingresar a la aplicación es **`devadmin`**.
Su contraseña se genera al preparar el entorno y está en el archivo privado:

```text
${HOME}/.config/cmdb/dev/app-password
```

Consultalo localmente con tu editor cuando necesites iniciar sesión. No es la
contraseña del usuario SQL ni la de `sa`.

La configuración se guarda por defecto en `~/.config/cmdb/dev/`:

| Archivo | Uso |
| --- | --- |
| `config.json` | Puertos y nombres del entorno |
| `app-password` | Contraseña funcional de `devadmin` |
| `db-password` | Contraseña SQL de `cmdb_dev_app`, usada por el backend |
| `sa-password` | Contraseña administrativa del motor, usada para inicializar |

El directorio tiene permisos 0700 y los secretos 0600. No copies estos archivos al
proyecto ni los compartas. `configure` conserva contraseñas existentes. Cambiar el
texto de un archivo de contraseña no cambia la contraseña de una identidad ya
creada en SQL Server: una rotación requiere actualizar ambos lados.

## Cambiar los puertos

Detené el backend y editá `~/.config/cmdb/dev/config.json`. Los valores iniciales
están en [infra/dev/config.example.json](infra/dev/config.example.json).

- `backend_port`: puerto HTTP del backend; por defecto 8081.
- `sql_port`: puerto SQL publicado en localhost; por defecto 14330.

Después ejecutá `up` y `backend` otra vez. Si cambia `backend_port`, actualizá
la URL configurada en el frontend y los comandos de comprobación.

## Detener, reiniciar y consultar estado

Detené primero el backend con **Ctrl+C** en su terminal. Luego, según necesites:

```bash
# Consultar estado del contenedor.
python3 scripts/dev/cmdb-dev.py status

# Verificar conexión, versión y objetos de la base.
python3 scripts/dev/cmdb-dev.py inspect

# Reiniciar SQL Server conservando los datos.
python3 scripts/dev/cmdb-dev.py restart

# Detener SQL Server y retirar el contenedor; conservar los datos.
python3 scripts/dev/cmdb-dev.py down
```

Los datos permanecen en el volumen Docker **`cmdb-dev-sql-data`**. La parada
habitual no lo elimina. El procedimiento de reset explícito está separado en
[la documentación de desarrollo](docs/CMDB-24.md#reset-explícito-y-destructivo).

## Pruebas

```bash
# Compilación y suite completa sobre SQL Server descartable.
./mvnw -B clean verify
python3 scripts/verify-test-inventory.py

# Con SQL de desarrollo disponible, JAR construido y backend detenido:
# comprobar lectura/escritura y persistencia al recrear el contenedor.
python3 scripts/dev/verify-persistence.py
```

La validación de persistencia detiene y recrea el contenedor de desarrollo,
conserva el volumen y retira el registro marcador al finalizar. Ejecutala cuando
no haya otra sesión usando ese backend.

## Problemas habituales

| Mensaje o síntoma | Qué revisar |
| --- | --- |
| `Unable to access jarfile` | Ejecutá `./mvnw -B clean verify` desde la raíz del proyecto. |
| No se puede conectar a Docker | Comprobá `docker info`, el servicio Docker y los permisos de tu usuario. |
| Puerto ocupado | Elegí un puerto libre en `config.json`; verificá que no haya otro backend iniciado. |
| Falta configuración privada | Ejecutá `python3 scripts/dev/cmdb-dev.py configure`. |
| SQL no está disponible | Ejecutá `up`, luego `status` e `inspect` antes de iniciar el backend. |
| Base existente no reconocida o versión distinta | Conservá el volumen e inspeccioná el estado; el inicializador se detiene para evitar reemplazar datos. |
| Frontend sin conexión | Comprobá la lectura con curl y que el frontend apunte al puerto y prefijo indicados. |

Para más detalles sobre esquema, permisos, secretos y evidencia, consultá
[CMDB-24](docs/CMDB-24.md). La configuración por ambientes y el contrato de secretos
están documentados en [CMDB-12](docs/CMDB-12.md) y [CMDB-13](docs/CMDB-13.md).
