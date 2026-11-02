# F20-T01 Integration Record

Date: 2026-11-02
Root repository: `CMDB` (workspace root)

## Pre-integration inventory

- Backend repository existed at `cmdb-backend/`.
- Backend branch: `main`.
- Backend HEAD before integration: `b19a566`.
- Frontend directory existed at `cmdb-frontend/`.
- Frontend Git repository: not found (`cmdb-frontend/.git` absent).

## Backups created

- Full backup folder: `CMDB-backup-<timestamp>`
- Backend full refs bundle: `CMDB-backup-<timestamp>/backend-all.bundle`
- Backend metadata and refs snapshot: `CMDB-backup-<timestamp>/backend-dotgit.tgz`
- Frontend files snapshot: `CMDB-backup-<timestamp>/frontend-files.tgz`
- Temporary pre-import workspace copy (includes original backend `.git`):
  `CMDB-preimport-<timestamp>`

## Import strategy

1. Initialized Git at repository root (`CMDB`).
2. Imported backend history with unrelated-histories merge and prefixed tree under `cmdb-backend/`.
3. Imported frontend as initial snapshot commit under `cmdb-frontend/`.

This preserved backend commit ancestry while relocating backend files to a component subdirectory in the monorepo.

## Integration commits

- `ebc2e0e` chore(repo): initialize CMDB monorepo root
- `d93b2b1` chore(repo): import backend history under cmdb-backend/
- `2c373a3` chore(repo): import frontend snapshot under cmdb-frontend/

Backend historical reference remains reachable through merge ancestry:

- `b19a566` chore: sanitized local history before first push

## Post-integration checks

- `git rev-parse --show-toplevel` from both component directories resolves to the `CMDB` workspace root.
- No nested `.git` directories remain under components in the working tree.
- No gitlinks (`160000` mode) present in index.

## Runtime validation executed

Validation was executed from clean clone:

- Clone path: `CMDB-validation-clone-<timestamp>`
- Backend command: `cd cmdb-backend && ./mvnw -B clean verify`
- Backend result: `BUILD SUCCESS` with `Tests run: 156, Failures: 0, Errors: 0, Skipped: 0`
- Inventory verifier: `cd cmdb-backend && python3 scripts/verify-test-inventory.py`
- Inventory verifier result: expected `156`, executed `156`, with no missing/unexpected methods
- Frontend check: `cmdb-frontend/public_html/index.html` present (`FRONTEND_ENTRYPOINT_OK`)

No remote push or remote destination modifications were performed.
