# Agent instructions (scope: docs/ and subdirectories)

## Scope and layout
- `API接口文档.md`
- `开发指南.md`
- `快速开始指南.md`
- `数据库设计.md`
- `部署文档.md`
- `代码生成指南.md`

## Update rules
- Backend config changes (DB/Redis/ports/context path) -> update `开发指南.md` and `部署文档.md`.
- Database schema or seed changes -> update `数据库设计.md`.
- Frontend proxy/baseURL changes -> update `快速开始指南.md`.
- Keep Windows-specific instructions in `部署文档.md` and `快速开始指南.md`.

## Do not
- Let docs conflict with actual config files; config files are the source of truth.
