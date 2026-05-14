package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class GmdbDatabaseIntegrationTestSupport {

    private static final String DATABASE_NAME = "gmdb";
    private static final String SUPERUSER = "gmdbuser";
    private static final String SUPERUSER_PASSWORD = "gmdbuser";
    private static final String APP_USER = "gmdb_app_user";
    private static final String APP_USER_PASSWORD = "gmdb_app_user";
    private static final String ADMIN_USER = "gmdb_admin";
    private static final String ADMIN_PASSWORD = "gmdb_admin";

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse(buildPostgresImage()).asCompatibleSubstituteFor("postgres"))
            .withDatabaseName(DATABASE_NAME)
            .withUsername(SUPERUSER)
            .withPassword(SUPERUSER_PASSWORD);

    private static final Path FILE_ROOT = createFileRoot();

    static {
        POSTGRES.start();
        GmdbLiquibaseMigrator.bootstrap(
                POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword(),
                APP_USER_PASSWORD,
                ADMIN_PASSWORD);
        GmdbLiquibaseMigrator.migrate(
                POSTGRES.getJdbcUrl(),
                ADMIN_USER,
                ADMIN_PASSWORD);
    }

    @DynamicPropertySource
    static void registerGmdbIntegrationProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.mybatis.r2dbc-url", GmdbDatabaseIntegrationTestSupport::r2dbcUrl);
        registry.add("spring.r2dbc.mybatis.username", () -> APP_USER);
        registry.add("spring.r2dbc.mybatis.password", () -> APP_USER_PASSWORD);
        registry.add("file-service.root", () -> FILE_ROOT.toString());
    }

    private static String r2dbcUrl() {
        return "r2dbc:postgresql://%s:%d/%s".formatted(
                POSTGRES.getHost(),
                POSTGRES.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
                POSTGRES.getDatabaseName());
    }

    private static String buildPostgresImage() {
        return new ImageFromDockerfile("gmdb-postgres-integration:" + GmdbLiquibaseMigrator.LIQUIBASE_VERSION, false)
                .withFileFromClasspath("Dockerfile", "gmdb-liquibase/docker/Dockerfile")
                .withFileFromClasspath(
                        "docker-entrypoint-initdb.d/000_enable_pg_jsonschema.sql",
                        "gmdb-liquibase/docker/docker-entrypoint-initdb.d/000_enable_pg_jsonschema.sql")
                .withFileFromClasspath(
                        "docker-entrypoint-initdb.d/010_create_liquibase_schema.sql",
                        "gmdb-liquibase/docker/docker-entrypoint-initdb.d/010_create_liquibase_schema.sql")
                .get();
    }

    private static Path createFileRoot() {
        try {
            return Files.createTempDirectory("gmdb-api-it-files-");
        } catch (final IOException exception) {
            throw new IllegalStateException("Could not create integration test file root", exception);
        }
    }
}
