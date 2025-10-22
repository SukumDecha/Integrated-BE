# Integrated-BE

A Spring Boot-based backend for an e-commerce application. This repository contains the server-side implementation (Java, Maven) with modular components for brands, users, orders, files, email, and security (JWT).

## Quick overview

- Language & framework: Java, Spring Boot (Maven)
- Build: Maven (pom.xml)
- Containerization: Dockerfile + docker-compose.yml, docker-compose.dev.yml
- Database: MySQL support (see `db/init.sql` and `db/my.cnf`)
- Main entrypoint: `src/main/java/sit/int202/ecommerce/ECommerceApplication.java`
- Profiles: `application.yml` and environment-specific files: `application-dev.yml`, `application-local.yml`, `application-prod.yml`, `application-uat.yml`

## Features / Modules

- brand — Brand management (controller, service, repository, DTOs, mappers)
- user — User account management, authentication handlers
- security — JWT authentication and related filters/providers (see `modules/security/jwt`)
- order — Order management and endpoints
- saleitem — Sale item management
- file — File upload/storage handlers and controllers (uploads stored under `uploads/`)
- email — Email sending services and templates (account verification template in `src/main/resources/templates`)

## Module-based architecture

This project follows a module-based folder structure under `src/main/java/sit/int202/ecommerce/modules`. Each feature or domain area is organized as a separate module package. This keeps code for controllers, services, repositories, DTOs and mappers colocated and makes it easier to reason about and maintain the codebase.

Typical module layout

- modules/<module-name>/
  - controller/   -> REST controllers and request mappings
  - service/      -> Business logic and transactional boundaries
  - repository/   -> Spring Data JPA repositories or DAOs
  - model/        -> JPA entities or domain models
  - dto/          -> Request/response DTOs used by controllers
  - mapper/       -> Mapping logic between entities and DTOs (MapStruct or manual mappers)
  - storage/      -> (optional) storage-related implementations (used by `file` module)

Responsibilities and data flow

- HTTP requests enter at a module's `controller` layer.
- Controllers validate input (DTOs) and forward to `service` methods.
- `service` contains business rules and coordinates repositories and other services. It should be the boundary for transactions.
- `repository` interfaces access the database; entities live in `model`.
- `mapper` classes convert between `model` entities and `dto` objects. The project provides a `ModelMapperConfig` to centralize mapping configs.

Cross-cutting modules

- `common` — shared DTOs, exceptions, and utility classes used across modules.
- `config` — application-wide configuration classes (properties, ModelMapper, web config, OpenAPI config).
- `security` — JWT token provider, filters, and security configuration. Typical components are under `modules/security/jwt`.

Conventions and how to add a new module

- Package naming: `sit.int202.ecommerce.modules.<module>`
- Keep the same sub-package structure (controller, service, repository, dto, mapper, model) to make it predictable.
- Add unit tests in `src/test/java/.../modules/<module>` matching the package structure.
- Keep controllers thin — put business logic in services.
- Register any new properties in `src/main/resources/application-*.yml` and bind them to `AppProperties` if application-wide.

Error handling and validation

- Centralized exception handlers live under `common/exceptions/handler` (see target compiled structure). Controllers should throw meaningful exceptions; handlers convert them to HTTP responses.
- Use DTO validation annotations (Jakarta/javax.validation) in request DTOs and let Spring MVC perform validation.

Uploads and file handling

- The `file` module handles uploads and storage. Runtime uploads are stored under the repository's `uploads/` directory (e.g., `uploads/SALE_ITEM/`, `uploads/USER_ACCOUNT/`). Ensure these folders exist and are writable in the runtime environment.

## Project structure (top-level)

- `src/main/java` — Application code
- `src/main/resources` — Spring configuration, templates
- `db/` — MySQL initialization script and config
- `docker-compose.yml`, `docker-compose.dev.yml` — Compose files for Docker-based runs
- `Dockerfile` — Builds the app container image
- `pom.xml` — Maven project descriptor
- `uploads/` — Runtime upload directories (e.g. `SALE_ITEM/`, `USER_ACCOUNT/`)

## Requirements

- Java 17+ (check project's `pom.xml` for the exact version)
- Maven 3.6+
- Docker & Docker Compose (if running containers)

## Quick start

Run with Maven:

```bash
# Run using the default profile (application.yml)
mvn spring-boot:run

# Run with a specific profile (dev/local/prod/uat)
mvn -Dspring-boot.run.profiles=dev spring-boot:run

# Build jar and run
mvn -DskipTests package
java -jar target/*.jar
```

Run with Docker Compose:

```bash
# Build and start containers (production-ish)
docker-compose up --build -d

# Development compose (if present)
docker-compose -f docker-compose.dev.yml up --build -d
```

Database initialization

- The `db/init.sql` contains schema/data initialization for MySQL used by the app when the DB container is first created.
- If using Docker Compose, the MySQL service will often mount `db/init.sql` so initialization runs automatically.

Configuration & environment

- Spring profiles: choose between `dev`, `local`, `prod`, `uat` via `SPRING_PROFILES_ACTIVE` or `-Dspring-boot.run.profiles`
- Application properties are under `src/main/resources/application-*.yml`.
- App-specific properties class: `sit.int202.ecommerce.config.AppProperties` (see `src/main/java/.../config/AppProperties.java`).
- JWT and security configuration live under `modules/security` (JWT providers, filters, tokens).

Testing

```bash
# Run unit/integration tests
mvn test
```

Useful commands

- Clean and build: `mvn clean package`
- Run tests: `mvn test`
- Run with specific profile: `mvn -Dspring-boot.run.profiles=dev spring-boot:run`

Development notes

- Uploads: uploaded files are stored in the `uploads/` directory (subfolders: `SALE_ITEM/`, `USER_ACCOUNT/`). Ensure these folders are writable by the runtime environment.
- Templates: Email templates are in `src/main/resources/templates`, e.g. `account-verification.html`.
- If you add or change database schema, update `db/init.sql` and corresponding JPA entities/repositories.

Where to look for things

- Controllers: `modules/**/controller`
- DTOs & mappers: `modules/**/dto` and `modules/**/mapper`
- Services & repositories: `modules/**/service` and `modules/**/repository`
- Security & JWT: `modules/security/jwt` (token provider, filters)

---

This README provides a concise reference for developers working on the backend. For environment-specific or deployment instructions, inspect the `docker-compose*.yml` files and the `application-*.yml` configurations.
