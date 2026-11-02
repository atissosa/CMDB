# CMDB-24 — SQL Server persistente para desarrollo

El backend dispone de una instancia local independiente de los tests:

| Elemento | Valor predeterminado |
| --- | --- |
| Imagen | `mcr.microsoft.com/mssql/server:2022-CU20-ubuntu-22.04` |
| Edición | Developer, exclusivamente desarrollo y pruebas |
| Proyecto Compose / servicio | `cmdb-dev` / `sqlserver` |
| Base / identidad SQL de aplicación | `cmdb_dev` / `cmdb_dev_app` |
| Puerto SQL del host | `127.0.0.1:14330`, configurable |
| Volumen Docker | `cmdb-dev-sql-data`, montado en `/var/opt/mssql` |
| Backend | perfil `dev`, `127.0.0.1:8081`, configurable |
| Configuración privada | `${HOME}/.config/cmdb/dev/` por defecto |
| Cuenta sintética de la aplicación | `devadmin`, contraseña generada localmente |

8080 está ocupado por Plane en esta máquina. La URL del backend de desarrollo es
`http://localhost:8081/cmdb-backend/recursos/`. Esta tarea no modifica el
frontend. Para una prueba posterior desde el navegador habrá que configurar allí
el puerto elegido; no se ha validado una sesión de navegador.

## Requisitos y licencia

Se requieren Docker Engine con Compose, Java 21, Maven Wrapper y memoria suficiente
para SQL Server (el motor se limita a 2048 MB, además del consumo del backend y los
servicios existentes). El primer arranque puede descargar la imagen y el driver.
Debe poder accederse al daemon Docker. El montaje persistente conserva las bases
cuando se elimina y recrea el contenedor.

Se configura `ACCEPT_EULA=Y` y `MSSQL_PID=Developer`: iniciar este entorno implica
aceptar los términos de Microsoft SQL Server para uso de desarrollo/pruebas.
Revisar los términos de la imagen antes de usarla fuera de ese contexto.
Referencias oficiales: [despliegue y conexión](https://learn.microsoft.com/en-us/sql/linux/sql-server-linux-docker-container-deployment?view=sql-server-ver17),
[persistencia y configuración](https://learn.microsoft.com/en-us/sql/linux/sql-server-linux-docker-container-configure),
[secretos en Compose](https://docs.docker.com/compose/how-tos/use-secrets/).

## Operación habitual

Desde `${HOME}/Projects/CMDB/cmdb-backend`:

```bash
# Primera vez: copia la plantilla y genera secretos sin mostrarlos.
python3 scripts/dev/cmdb-dev.py configure

# Arranca, espera salud SQL, inicializa solo una base nueva e inspecciona.
python3 scripts/dev/cmdb-dev.py up
python3 scripts/dev/cmdb-dev.py status
python3 scripts/dev/cmdb-dev.py inspect

# Con el JAR construido, backend en primer plano; Ctrl+C para detenerlo.
python3 scripts/dev/cmdb-dev.py backend

# Reinicia el motor sin eliminar el volumen; detener antes el backend.
python3 scripts/dev/cmdb-dev.py restart

# Detiene y elimina contenedor/red; los datos y secretos se conservan.
python3 scripts/dev/cmdb-dev.py down
```

`infra/dev/config.example.json` es una plantilla sin contraseñas. La copia privada
`config.json` permite cambiar `sql_port` y `backend_port` sin tocar el repositorio.
Los nombres de base e identidades se mantienen fijos para evitar inicializar por
error otra base. `CMDB_DEV_CONFIG_DIR` permite elegir otro directorio privado,
pero usa el mismo volumen/proyecto local: no crea una segunda instalación.
Antes del primer arranque se comprueba el puerto y la propiedad del volumen. Si el
puerto está ocupado o el volumen tiene otro propietario, se detiene la operación.

`backend` inyecta `CMDB_BD_SERVIDOR`, `CMDB_BD_PUERTO`, `CMDB_BD_NOMBRE`,
`CMDB_DB_USERNAME` y `CMDB_DB_PASSWORD` en el proceso Java, conforme a CMDB-12/13.
Elimina posibles overrides heredados CMDB/SPRING y opciones Java globales para que
este lanzador local use exclusivamente el entorno dev configurado. El perfil dev
usa la configuración TLS local existente. No modifica configuración prod ni los
perfiles de la suite.

## Secretos e identidades

El directorio privado tiene permisos 0700 y los archivos 0600:

- `sa-password`: contraseña administrativa generada para este motor.
- `db-password`: contraseña independiente de `cmdb_dev_app`.
- `app-password`: contraseña sintética de `devadmin`, para el login funcional.

Las contraseñas no están en Compose, scripts, argumentos de procesos ni ejemplos.
Compose monta solo la contraseña administrativa como secreto de archivo; un
entrypoint la lee y baja a UID/GID 10001 antes de ejecutar el motor. El motor recibe
su variable de entorno, y la aplicación la suya, pero no se publica el valor en la
configuración de Docker ni en la línea de comandos. Los usuarios con acceso al
host/daemon Docker pueden acceder a los secretos: es un entorno local, no una
bóveda cifrada. No editar las contraseñas de archivos esperando que eso cambie las
identidades SQL de una base existente; una rotación debe coordinarse explícitamente.

`cmdb_dev_app` no es sysadmin ni db_owner. Tiene lectura, escritura y EXECUTE en
el esquema dbo de `cmdb_dev`, suficientes para los mapeadores y SP actuales.
Las operaciones administrativas de inicialización usan una conexión separada sa.
El mecanismo de contraseña de usuarios funcionales es el existente en legacy;
esta tarea no rediseña autenticación. Ninguna credencial histórica se reutiliza.

## Inicialización y conservación de datos

`infra/dev/sql/01-schema.sql` conserva los cuerpos SQL de tablas, vistas, triggers
y procedimientos del fixture legacy inventariado. Únicamente se retira el prólogo
que hacía USE master / DROP / CREATE / USE CMDB; la conexión JDBC selecciona
explícitamente `cmdb_dev`. `origin.json` registra el origen y ambos hashes.
No hay DROP DATABASE en el arranque.

`02-seed.sql` crea solo un departamento sintético Mesa de Ayuda, un rol
Administrador y el catálogo operativo de 24 permisos ya definido en legacy.
El inicializador inserta un único usuario `devadmin` con contraseña generada y
parámetros JDBC. No importa el dump de datos histórico ni fixtures de equipos,
clientes, direcciones, personas o contraseñas de la entrega.

Si la base no existe, se crea y la estructura, datos sintéticos, identidad SQL y
marca de versión se inicializan en una transacción. Si falla, no se marca como
completa. Una base ya existente solo se acepta con una marca y hashes de scripts
coincidentes; no se vuelve a sembrar ni se cambia su contraseña. Una base ajena,
incompleta o con versión distinta se rechaza para inspección/migración manual.
No se intenta repararla borrándola. `inspect` verifica conexión del usuario SQL
limitado, objetos y versión del motor.

### Reset explícito y destructivo

La parada habitual **nunca** utiliza `--volumes`. Si se decide perder todos los
datos de desarrollo, detener primero el backend, comprobar que el volumen es
`cmdb-dev-sql-data` y ejecutar de forma explícita:

```bash
python3 scripts/dev/cmdb-dev.py down
docker volume inspect cmdb-dev-sql-data
# DESTRUCTIVO: elimina exclusivamente la base persistente local de desarrollo.
docker volume rm cmdb-dev-sql-data
python3 scripts/dev/cmdb-dev.py up
```

Conservar los archivos privados permite recrear las identidades con las mismas
contraseñas. No se ejecutó este reset durante la implementación. Para datos útiles,
realizar primero un respaldo SQL Server; el volumen persistente no sustituye un backup.

## Validación reproducible

```bash
python3 scripts/dev/verify-persistence.py
./mvnw -B clean verify
python3 scripts/verify-test-inventory.py
```

La primera validación arranca el JAR con dev y la identidad SQL limitada, hace
login sintético, crea/lee un departamento marcador mediante HTTP, detiene backend
y Compose, recrea el contenedor conservando el volumen y vuelve a leer el mismo
registro desde un backend nuevo. Retira el marcador y verifica que los conteos y
la marca del esquema vuelvan al estado inicial. No modifica registros de usuario.
Deja SQL Server disponible y detiene sus procesos de backend al terminar.

La suite Maven continúa usando `SqlServerTesting` y Testcontainers con base
`CMDB` en otro contenedor y puerto aleatorio. El lanzador dev no forma parte de
la suite ni cambia sus recursos. La validación registra el inventario completo y
compara el estado persistente antes/después de Maven.

Evidencia en `docs/evidence/cmdb24/`: primer arranque, validación HTTP y de
persistencia, suite/inventario, aislamiento, inspección de infraestructura y secretos,
y diff frente al respaldo. Se conserva el primer intento fallido: la comprobación
de puerto confundía sockets TIME_WAIT con un servicio activo al reabrir el backend.
Se corrigió con SO_REUSEADDR antes de repetir la validación; no se reseteó la base.

Resultado final: SQL Server 16.0.4205.1 disponible y saludable, 23 tablas (incluida
la marca de inicialización), 14 vistas y 13 procedimientos. El flujo HTTP y la
persistencia al recrear el contenedor pasaron; marcador retirado. Maven terminó
BUILD SUCCESS con 148 métodos, sin fallos, errores, omitidos, faltantes ni
duplicados. Testcontainers se eliminó y la base persistente quedó disponible,
sin cambios en sus conteos ni marca de esquema durante la suite. El backend
de validación quedó detenido; el comando `backend` lo inicia en primer plano.
