package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class GmdbDatabaseIntegrationTestSupport {

    private static final String DATABASE_NAME = "gmdb";
    private static final String BASELINE_TEST_DATA_SCRIPT = "test-data.sql";
    private static final String SUPERUSER = "gmdbuser";
    private static final String SUPERUSER_PASSWORD = "gmdbuser";
    private static final String APP_USER = "gmdb_app_user";
    private static final String APP_USER_PASSWORD = "gmdb_app_user";
    private static final String ADMIN_USER = "gmdb_admin";
    private static final String ADMIN_PASSWORD = "gmdb_admin";

    protected static GmdbIntegrationDatabase createStartedDatabase() {
        final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
                DockerImageName.parse(buildPostgresImage()).asCompatibleSubstituteFor("postgres"))
                .withDatabaseName(DATABASE_NAME)
                .withUsername(SUPERUSER)
                .withPassword(SUPERUSER_PASSWORD);

        postgres.start();
        GmdbLiquibaseMigrator.bootstrap(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                APP_USER_PASSWORD,
                ADMIN_PASSWORD);
        GmdbLiquibaseMigrator.migrate(
                postgres.getJdbcUrl(),
                ADMIN_USER,
                ADMIN_PASSWORD);

        return new GmdbIntegrationDatabase(postgres, createFileRoot());
    }

    protected static void applyBaselineTestData(final GmdbIntegrationDatabase database) {
        synchronized (database.baselineTestDataLock) {
            if (!database.baselineTestDataApplied) {
                executeSqlScript(database, BASELINE_TEST_DATA_SCRIPT);
                database.baselineTestDataApplied = true;
            }
        }
    }

    protected static int queryForInt(final GmdbIntegrationDatabase database, final String sql) {
        try (
                Connection connection = DriverManager.getConnection(
                        database.postgres.getJdbcUrl(),
                        APP_USER,
                        APP_USER_PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
        ) {
            if (!resultSet.next()) {
                throw new IllegalStateException("Query returned no rows: " + sql);
            }
            return resultSet.getInt(1);
        } catch (final Exception exception) {
            throw new IllegalStateException("Could not query integration test database", exception);
        }
    }

    protected static void registerGmdbIntegrationProperties(
            final DynamicPropertyRegistry registry,
            final GmdbIntegrationDatabase database) {

        registry.add("spring.r2dbc.mybatis.r2dbc-url", database::r2dbcUrl);
        registry.add("spring.r2dbc.mybatis.username", () -> APP_USER);
        registry.add("spring.r2dbc.mybatis.password", () -> APP_USER_PASSWORD);
        registry.add("file-service.root", () -> database.fileRoot.toString());
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

    private static void executeSqlScript(final GmdbIntegrationDatabase database, final String resourceName) {
        try (
                Connection connection = DriverManager.getConnection(
                        database.postgres.getJdbcUrl(),
                        SUPERUSER,
                        SUPERUSER_PASSWORD);
                Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(false);
            statement.execute(readClasspathResource(resourceName));
            connection.commit();
        } catch (final Exception exception) {
            throw new IllegalStateException("Could not apply " + resourceName, exception);
        }
    }

    private static String readClasspathResource(final String resourceName) throws IOException {
        try (
                InputStream inputStream = GmdbDatabaseIntegrationTestSupport.class
                        .getClassLoader()
                        .getResourceAsStream(resourceName)
        ) {
            if (inputStream == null) {
                throw new IllegalStateException("Could not find classpath resource " + resourceName);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Path createFileRoot() {
        try {
            return Files.createTempDirectory("gmdb-api-it-files-");
        } catch (final IOException exception) {
            throw new IllegalStateException("Could not create integration test file root", exception);
        }
    }

    protected static final class GmdbIntegrationDatabase {

        private final PostgreSQLContainer<?> postgres;
        private final Path fileRoot;
        private final Object baselineTestDataLock = new Object();
        private boolean baselineTestDataApplied;

        private GmdbIntegrationDatabase(final PostgreSQLContainer<?> postgres, final Path fileRoot) {
            this.postgres = postgres;
            this.fileRoot = fileRoot;
        }

        private String r2dbcUrl() {
            return "r2dbc:postgresql://%s:%d/%s".formatted(
                    postgres.getHost(),
                    postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
                    postgres.getDatabaseName());
        }
    }
}
