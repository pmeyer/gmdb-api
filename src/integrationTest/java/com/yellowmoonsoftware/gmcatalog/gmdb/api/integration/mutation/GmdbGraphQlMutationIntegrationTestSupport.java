package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.mutation;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.GmdbDatabaseIntegrationTestSupport;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
@AutoConfigureWebTestClient(timeout = "30s")
@ActiveProfiles("test")
abstract class GmdbGraphQlMutationIntegrationTestSupport extends GmdbDatabaseIntegrationTestSupport {

    protected static GmdbIntegrationDatabase createStartedMutationDatabase() {
        final var database = createStartedDatabase();
        applyBaselineTestData(database);
        return database;
    }

    protected static void registerMutationIntegrationProperties(
            final DynamicPropertyRegistry registry,
            final GmdbIntegrationDatabase database) {
        registerGmdbIntegrationProperties(registry, database);
    }
}
