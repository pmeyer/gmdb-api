package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
    private static final Object BASELINE_TEST_DATA_LOCK = new Object();

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse(buildPostgresImage()).asCompatibleSubstituteFor("postgres"))
            .withDatabaseName(DATABASE_NAME)
            .withUsername(SUPERUSER)
            .withPassword(SUPERUSER_PASSWORD);

    private static final Path FILE_ROOT = createFileRoot();
    private static boolean baselineTestDataApplied;

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

    protected static void applyBaselineTestData() {
        synchronized (BASELINE_TEST_DATA_LOCK) {
            if (!baselineTestDataApplied) {
                executeSqlScript(BASELINE_TEST_DATA_SCRIPT);
                baselineTestDataApplied = true;
            }
        }
    }

    protected static int queryForInt(final String sql) {
        try (
                Connection connection = DriverManager.getConnection(
                        POSTGRES.getJdbcUrl(),
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

    private static void executeSqlScript(final String resourceName) {
        try (
                Connection connection = DriverManager.getConnection(
                        POSTGRES.getJdbcUrl(),
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
}
