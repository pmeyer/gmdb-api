package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.query;

import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
@AutoConfigureWebTestClient(timeout = "30s")
@ActiveProfiles("test")
abstract class GmdbGraphQlQueryIntegrationTestSupport extends GmdbReadOnlyIntegrationTestSupport {
}
