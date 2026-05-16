package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

class TranscriberSearchQueryIntegrationTests extends GmdbGraphQlQueryIntegrationTestSupport {

    private static final GmdbIntegrationDatabase DATABASE = createStartedDatabase();

    @Autowired
    private WebGraphQlTester graphQlTester;

    @BeforeAll
    static void applyTestData() {
        applyBaselineTestData(DATABASE);
    }

    @DynamicPropertySource
    static void registerGmdbIntegrationProperties(final DynamicPropertyRegistry registry) {
        registerGmdbIntegrationProperties(registry, DATABASE);
    }

    @Test
    void transcriberSearchWithoutSearchNameReturnsExpectedTranscribers() {
        final var results = graphQlTester.document("""
                        query {
                            transcriberSearch {
                                name
                            }
                        }
                        """)
                .execute()
                .path("transcriberSearch")
                .entityList(TranscriberResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                transcriber("Andy Aledort"),
                transcriber("Danny Begelman"),
                transcriber("Dave Whitehill"),
                transcriber("Jeff Perrin"));
    }

    @Test
    void transcriberSearchWithSearchNameMatchesTranscriberNameCaseInsensitively() {
        final var results = graphQlTester.document("""
                        query {
                            transcriberSearch(searchName: "dave") {
                                name
                            }
                        }
                        """)
                .execute()
                .path("transcriberSearch")
                .entityList(TranscriberResponse.class)
                .get();

        assertThat(results).containsExactly(
                transcriber("Dave Whitehill"));
    }

    @Test
    void transcriberSearchWithPartialSearchNameReturnsMatchingTranscribers() {
        final var results = graphQlTester.document("""
                        query {
                            transcriberSearch(searchName: "an") {
                                name
                            }
                        }
                        """)
                .execute()
                .path("transcriberSearch")
                .entityList(TranscriberResponse.class)
                .get();

        assertThat(results).containsExactlyInAnyOrder(
                transcriber("Andy Aledort"),
                transcriber("Danny Begelman"));
    }

    private static TranscriberResponse transcriber(final String name) {
        return new TranscriberResponse(name);
    }

    private record TranscriberResponse(String name) {
    }
}
