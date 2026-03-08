# Agent instructions (scope: database/ and subdirectories)

## Scope and layout
- `schema.sql` (DDL)
- `data.sql` (seed data)

## Usage
- Import order: `schema.sql` first, then `data.sql`.
- Default DB name: `herb_platform` (matches backend dev config).

## Windows notes
- Ensure MySQL uses UTF-8/utf8mb4 to avoid Chinese text issues.

## Do not
- Change schema without updating backend entities/mappers and `docs/数据库设计.md`.
