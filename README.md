# CMDB Monorepo

This repository contains both CMDB components:

- `cmdb-backend/` (Spring Boot + Maven)
- `cmdb-frontend/` (static frontend with legacy Node/Bower tooling files)

## Working model

- Keep each component build/run workflow inside its own directory.
- Do not move backend Maven files (`pom.xml`, `src/`) to repository root.
- Do not mix frontend and backend build artifacts.

## Backend

From `cmdb-backend/`:

```bash
./mvnw -B clean verify
python3 scripts/verify-test-inventory.py
```

## Frontend

From `cmdb-frontend/`, load the application entrypoint currently used by the project:

- `public_html/index.html`

Any additional frontend build/test command should be executed only if that toolchain is present in this component.

## Notes

- Historical backend commits are preserved and imported under `cmdb-backend/` in this monorepo.
- Frontend was imported as an initial snapshot because no frontend Git repository was found in `cmdb-frontend/`.
