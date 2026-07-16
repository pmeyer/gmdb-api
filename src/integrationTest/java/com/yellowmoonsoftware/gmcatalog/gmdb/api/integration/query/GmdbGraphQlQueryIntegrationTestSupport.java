package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.query;

import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
@AutoConfigureWebTestClient(timeout = "30s")
@ActiveProfiles("test")
abstract class GmdbGraphQlQueryIntegrationTestSupport extends GmdbReadOnlyIntegrationTestSupport {
}
