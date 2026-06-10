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
