# CMDB Monorepo

Este repositorio contiene el sistema CMDB (Configuration Management Database),
compuesto por un backend Java y un frontend legacy.

## Que resuelve este sistema

CMDB centraliza la gestion de configuracion e inventario tecnico. Permite,
entre otros flujos:

- Gestionar lugares (departamentos centrales, subagencias y services).
- Gestionar activos/equipos y su estado operativo.
- Gestionar prestamos, reparaciones e historicos asociados.
- Exponer API HTTP para consumo del cliente web.

## Estructura del repositorio

- cmdb-backend/: API y logica de negocio (Spring Boot, Maven, SQL Server).
- cmdb-frontend/: cliente web estatico legacy.

## Requisitos de instalacion

- JDK 21.
- Python 3.
- Docker Engine con Docker Compose habilitado.

Verificacion rapida:

```bash
java -version
python3 --version
docker info
docker compose version
```

## Instalacion y puesta en marcha

### 1) Backend

Desde cmdb-backend:

```bash
cd cmdb-backend
python3 scripts/dev/cmdb-dev.py configure
python3 scripts/dev/cmdb-dev.py up
./mvnw -B clean verify
python3 scripts/dev/cmdb-dev.py backend
```

Backend disponible por defecto en:

- http://localhost:8081/cmdb-backend/recursos/

Chequeo basico de API:

```bash
curl --fail-with-body -i \
	http://localhost:8081/cmdb-backend/recursos/departamento
```

### 2) Frontend

Desde cmdb-frontend:

```bash
cd cmdb-frontend/public_html
python3 -m http.server 18080
```

Abrir en navegador:

- http://127.0.0.1:18080/index.html

Nota: si el servidor se ejecuta desde cmdb-frontend (raiz) en lugar de
public_html, la ruta correcta pasa a ser /public_html/index.html.

## Validacion y pruebas

Desde cmdb-backend:

```bash
./mvnw -B clean verify
python3 scripts/verify-test-inventory.py
```

## Modelo de trabajo en monorepo

- Mantener el flujo de build/run de cada componente dentro de su carpeta.
- No mover archivos Maven del backend (pom.xml, src/) a la raiz.
- No mezclar artefactos de build entre frontend y backend.

## Notas

- El historial del backend fue preservado e importado bajo cmdb-backend/.
- El frontend fue incorporado como snapshot inicial porque no se encontro un
	repositorio Git propio dentro de cmdb-frontend/.
