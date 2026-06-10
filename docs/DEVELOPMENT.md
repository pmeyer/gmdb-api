# Development

## Technology Stack

- Java 23
- Spring Boot 3.5.x
- Spring WebFlux
- Spring GraphQL
- MyBatis through `mybatis-r2dbc`
- PostgreSQL
- GraphQL multipart upload support through `graphql-multipart-fileupload`
- File-system backed resource storage by default, with the storage layer abstracted for future alternatives

## Repository Guidelines

Primary source lives in `src/main/java/com/yellowmoonsoftware/gmcatalog/gmdb/api`. Controllers expose GraphQL endpoints in
`controller/`; GraphQL adapters sit under `graphql/`; cross-cutting config is in `config/`; services and mappers drive
persistence and file IO. GraphQL schemas live in `src/main/resources/graphql/*.graphqls` and should be updated alongside
Java resolvers. Tests belong in `src/test/java`, mirroring the main package structure when adding fixtures.

Use Java 23, four-space indentation, and IDE-default import ordering. Prefer immutable `record` types for DTOs, keep
Lombok annotations such as `@RequiredArgsConstructor` instead of adding manual boilerplate, and use Java streams for
collection processing where practical. Controllers should return Reactor `Mono`/`Flux`; avoid blocking calls and use
`ReactiveUtils.async(...)` when bridging blocking mapper work.

Tests use JUnit 5, Spring Boot test starters, AssertJ assertions, and Reactor `StepVerifier` where applicable. Test classes
should be named `*Tests`. Keep tests focused and deterministic, define mocks at class level with annotations where
possible, and avoid mocking simple DTOs, `ObjectMapper`, static methods, final classes, or other hard-to-mock constructs.

Every commit must follow Conventional Commits, including normal commits, squash-merge commits, and merge commits. Before
merging a PR, verify that the final merge or squash commit subject is conventional. Prefer squash merges for large PRs so
the main branch keeps a concise, reviewable history. PRs should describe context, list notable changes, and call out schema
updates or required migrations.

Database and file-store credentials are sourced through `application.yml`; prefer environment overrides for secrets. When
touching file upload logic, verify `FileResourceConfiguration` and `ResourceSlug` paths to keep stored resources
predictable and safe.

## Maven Package Access

Test-scoped database migration resources are provided by the public `gmdb-liquibase` Maven package hosted in GitHub
Packages. Maven resolves that artifact from `https://maven.pkg.github.com/pmeyer/gmdb-liquibase` using the repository id
`github`.

Configure credentials in `~/.m2/settings.xml` before running a build that needs to resolve the package:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>${env.GITHUB_ACTOR}</username>
      <password>${env.GITHUB_TOKEN}</password>
    </server>
  </servers>
</settings>
```

For local builds that need GitHub Packages authentication, set `GITHUB_ACTOR` to the GitHub username associated with the
PAT and set `GITHUB_TOKEN` to a PAT with package read access.

In GitHub Actions, prefer `GITHUB_ACTOR` and `GITHUB_TOKEN` or a repository secret with equivalent package read access.

## Tests

Run the full test suite with:

```shell
mvn verify
```

Integration tests live under `src/integrationTest` and run during `mvn verify` through Maven Failsafe. Database-backed
fixtures use Testcontainers to build the PostgreSQL image from the `gmdb-liquibase` test resources artifact, start a
container, apply the bootstrap changelog, and then apply the migration changelog before the Spring application context
starts.

Docker must be available when running integration tests.
