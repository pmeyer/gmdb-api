package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

final class GmdbLiquibaseMigrator {

    static final String LIQUIBASE_VERSION = "1.3.2";

    private static final String BOOTSTRAP_CHANGELOG = "db/changelog/bootstrap-changelog.xml";
    private static final String MIGRATE_CHANGELOG = "db/changelog/db-changelog.xml";
    private static final String APP_PASSWORD_PARAMETER = "gmdb.app.password";
    private static final String ADMIN_PASSWORD_PARAMETER = "gmdb.admin.password";

    private GmdbLiquibaseMigrator() {
    }

    static void bootstrap(
            final String jdbcUrl,
            final String username,
            final String password,
            final String appPassword,
            final String adminPassword) {

        update(
                jdbcUrl,
                username,
                password,
                BOOTSTRAP_CHANGELOG,
                Map.of(
                        APP_PASSWORD_PARAMETER, appPassword,
                        ADMIN_PASSWORD_PARAMETER, adminPassword));
    }

    static void migrate(final String jdbcUrl, final String username, final String password) {
        update(jdbcUrl, username, password, MIGRATE_CHANGELOG, Map.of());
    }

    private static void update(
            final String jdbcUrl,
            final String username,
            final String password,
            final String changelog,
            final Map<String, String> parameters) {

        try (
                Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor()
        ) {
            final Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setLiquibaseSchemaName("liquibase");

            final Liquibase liquibase = new Liquibase(changelog, resourceAccessor, database);
            parameters.forEach((name, value) -> liquibase.getChangeLogParameters().set(name, value));
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (final Exception exception) {
            throw new IllegalStateException("Could not apply " + changelog, exception);
        }
    }
}
