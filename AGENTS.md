# Repository Guidelines

## Project Structure & Module Organization
- Primary source lives in `src/main/java/com/yellowmoon/gmdb`. Controllers expose GraphQL endpoints in `controller/`; GraphQL adapters sit under `graphql/`; cross-cutting config is in `config/`; services and mappers drive persistence and file IO.
- Shared DTO records reside in `dto` subpackages; `util/` provides Reactor helpers.
- GraphQL schemas are defined in `src/main/resources/graphql/*.graphqls`; adjust them alongside Java resolvers.
- Static assets and configuration (`application.yml`, GraphiQL assets) also sit under `src/main/resources`. Build output collects in `target/` and should remain untracked.
- Tests belong in `src/test/java`; mirror the main package structure when adding fixtures.

## Build, Test, and Development Commands
- `./mvnw clean package` builds the Spring Boot artifact and runs unit tests.
- `./mvnw spring-boot:run` starts the API with GraphQL and WebFlux endpoints; use `SPRING_PROFILES_ACTIVE` to switch configs.
- `./mvnw test` executes the current test suite; `./mvnw verify` adds integration checks when defined.

## Coding Style & Naming Conventions
- Use Java 23, four-space indentation, and keep imports ordered via IDE defaults. Prefer immutable `record` types for DTOs, matching existing code.
- Controllers return Reactor `Mono`/`Flux`; avoid blocking calls and lean on `ReactiveUtils.async(...)` when bridging blocking mappers.
- Lombok annotations (`@RequiredArgsConstructor`, builder support) are standard—retain them instead of manual boilerplate.
- GraphQL schema fields should map to camelCase resolver methods (e.g., `@SchemaMapping(field = "details")` → `details`).
- Prefer the use of Java streams for processing data in collections over imperative looping constructs.

## Testing Guidelines
- Create tests with JUnit 5 and Spring Boot test starters; GraphQL components can use `@GraphQlTest` or `WebGraphQlTester`.
- Place Reactor behavior behind `StepVerifier` assertions; use `mybatis-spring-boot-starter-test` for mapper slices.
- Name test classes `*Tests`; keep focused, deterministic fixtures, and include representative resource files under `src/test/resources` when needed.
- Use assertj for assertions
- Test classes in isolation, creating mocks for any dependencies that provide behavior to the class under test. Dependencies that just hold data can be used directly.
- Mocks used should be defined at class level using annotations.

## Commit & Pull Request Guidelines
- Follow the repository’s short, imperative commit messages (e.g., `Add mapper for albums`). Group related edits together instead of squashing unrelated fixes.
- PRs should describe context, list notable changes, and call out schema updates or required migrations. Link Jira/GitHub issues when available.
- Include validation evidence (commands run, screenshots of GraphiQL output) and flag configuration changes so reviewers can reproduce locally.

## Security & Configuration Tips
- Database and file-store credentials are sourced via `application.yml`; prefer environment overrides for secrets.
- When touching file upload logic, verify `FileResourceConfiguration` and `ResourceSlug` paths to keep stored resources predictable and safe.
