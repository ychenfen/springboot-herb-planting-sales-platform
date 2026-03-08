# Agent instructions (scope: backend/ and subdirectories)

## Scope and layout
- Key directories:
  - `src/main/java/com/herb/platform/` (application code)
  - `src/main/resources/` (config, mapper XML, static assets)
  - `src/test/java/` (tests)
- Entry point: `com.herb.platform.HerbPlatformApplication`

## Commands
- Install/build: `mvn clean package`
- Dev run: `mvn spring-boot:run`
- Tests: `mvn test`
- Profiles:
  - Default is `dev` from `src/main/resources/application.yml`.
  - Use `-Dspring-boot.run.profiles=prod` for prod runs.

## Config & data sources
- Dev DB: `jdbc:mysql://localhost:3306/herb_platform` in `src/main/resources/application-dev.yml`.
- Dev Redis: `localhost:6379` in `src/main/resources/application-dev.yml`.
- Prod uses environment variables in `application-prod.yml`; do not hardcode secrets.

## Windows notes
- JDK 11 is required; ensure `JAVA_HOME` and `mvn -v` work.
- Update `herb.file.storage-path` (and `herb.file.access-path` if needed) in
  `src/main/resources/application.yml` to a Windows-friendly path (example: `C:/data/uploads`).
- `application-prod.yml` uses `LOG_PATH` for log location; set it to a Windows path when deploying.

## Common pitfalls
- Context path is `/api`; changing it requires frontend proxy/baseURL updates.
- DB name must match `herb_platform` unless configs and docs are updated together.

## Do not
- Commit real credentials into `application-*.yml`.
