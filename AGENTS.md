# Agent instructions (scope: repo root and subdirectories)

## Scope and layout
- This AGENTS.md applies to: `/` and below.
- Key directories:
  - `backend/` (Spring Boot + MyBatis, Redis, JWT)
  - `frontend/` (Vue 3 + Vite)
  - `database/` (MySQL schema + seed data)
  - `docs/` (project documentation)

## Modules / subprojects

| Module | Type | Path | What it owns | How to run | Tests | Docs | AGENTS |
|--------|------|------|--------------|------------|-------|------|--------|
| backend | spring-boot (maven) | `backend/` | REST API, auth/JWT, MyBatis, Redis | `mvn spring-boot:run` | `mvn test` | `docs/开发指南.md`, `docs/部署文档.md`, `docs/API接口文档.md` | `backend/AGENTS.md` |
| frontend | vue + vite | `frontend/` | Web UI, client state | `npm run dev` | (none) | `frontend/README.md` | `frontend/AGENTS.md` |
| database | mysql sql | `database/` | Schema + seed data | `schema.sql` then `data.sql` | (none) | `docs/数据库设计.md` | `database/AGENTS.md` |
| docs | docs | `docs/` | Project docs | n/a | n/a | `docs/` | `docs/AGENTS.md` |

## Cross-domain workflows
- Frontend -> backend API:
  - Frontend base URL is `/api` in `frontend/src/utils/request.js`.
  - Dev proxy is in `frontend/vite.config.js` to `http://localhost:8080`.
  - Backend context path is `/api` in `backend/src/main/resources/application.yml`.
  - Keep these three aligned when changing ports/paths.
- Auth/session:
  - Frontend sends `Authorization: Bearer <token>` in `frontend/src/utils/request.js`.
  - Backend JWT config is in `backend/src/main/resources/application.yml`.
- Database source of truth:
  - Connection settings live in `backend/src/main/resources/application-*.yml`.
  - Schema/seed are in `database/schema.sql` and `database/data.sql`.
  - Frontend must not store DB connection details.
- Windows setup documentation:
  - Record Windows-specific changes in `docs/部署文档.md` and `docs/快速开始指南.md`.
  - Keep DB name/host/user/password notes consistent with backend config files.

## Verification
- Prefer running module-local commands from the module folder.
- Run quiet first; re-run narrowed failures with verbose logs only when debugging.

## Docs usage
- Do not open/read `docs/` unless the task requires it or the user asks.

## Do not
- Put module-specific command details here; keep them in module `AGENTS.md`.
- Change DB names/ports without updating both backend config and docs.
