package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("test")
abstract class GmdbGraphQlQueryIntegrationTestSupport extends GmdbDatabaseIntegrationTestSupport {
}
