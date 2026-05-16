package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType.BOOK;
import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType.MAG;
import static org.assertj.core.api.Assertions.assertThat;

class GetPubIndicesQueryIntegrationTests extends GmdbGraphQlQueryIntegrationTestSupport {

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
    void getPubIndicesWithoutCriteriaReturnsExpectedPublicationIndices() {
        final var results = graphQlTester.document("""
                        query {
                            getPubIndices {
                                name
                                type
                                serialNumber
                            }
                        }
                        """)
                .execute()
                .path("getPubIndices")
                .entityList(PubIndexResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubIndex("Guitar For The Practicing Musician", MAG, "0738937X"),
                pubIndex("Guitar World", MAG, "10456295"),
                pubIndex("Tom Petty & the Heartbreakers: Greatest Hits", BOOK, "0898987660"));
    }

    @Test
    void getPubIndicesWithBookTypeReturnsBookPublicationIndices() {
        final var results = graphQlTester.document("""
                        query {
                            getPubIndices(criteria: { type: BOOK }) {
                                name
                                type
                                serialNumber
                            }
                        }
                        """)
                .execute()
                .path("getPubIndices")
                .entityList(PubIndexResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubIndex("Tom Petty & the Heartbreakers: Greatest Hits", BOOK, "0898987660"));
    }

    @Test
    void getPubIndicesWithMagazineTypeReturnsMagazinePublicationIndices() {
        final var results = graphQlTester.document("""
                        query {
                            getPubIndices(criteria: { type: MAG }) {
                                name
                                type
                                serialNumber
                            }
                        }
                        """)
                .execute()
                .path("getPubIndices")
                .entityList(PubIndexResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubIndex("Guitar For The Practicing Musician", MAG, "0738937X"),
                pubIndex("Guitar World", MAG, "10456295"));
    }

    private static PubIndexResponse pubIndex(
            final String name,
            final PubType type,
            final String serialNumber) {

        return new PubIndexResponse(name, type, serialNumber);
    }

    private record PubIndexResponse(String name, PubType type, String serialNumber) {
    }
}
