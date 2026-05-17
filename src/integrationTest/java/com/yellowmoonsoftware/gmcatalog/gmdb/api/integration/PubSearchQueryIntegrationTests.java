package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;

import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType.BOOK;
import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType.MAG;
import static org.assertj.core.api.Assertions.assertThat;

class PubSearchQueryIntegrationTests extends GmdbGraphQlQueryIntegrationTestSupport {

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
    void pubSearchWithoutCriteriaReturnsExpectedPublicationsInDefaultOrder() {
        final var results = graphQlTester.document("""
                        query {
                            pubSearch {
                                name
                                type
                                pubDate
                                serialNumber
                                details {
                                    ... on MagDetails {
                                        issueName
                                    }
                                    ... on BookDetails {
                                        edition
                                    }
                                }
                            }
                        }
                        """)
                .execute()
                .path("pubSearch")
                .entityList(PubResponse.class)
                .get();

        assertThat(results).containsExactly(
                pub("Guitar For The Practicing Musician", MAG, "1988-12-01", "0738937X", details("December 1988", null)),
                pub("Tom Petty & the Heartbreakers: Greatest Hits", BOOK, "1994-04-01", "0898987660", details(null, "First Printing")),
                pub("Guitar World", MAG, "2018-11-01", "10456295", details("November 2018", null)),
                pub("Guitar World", MAG, "2018-12-01", "10456295", details("Holiday 2018", null)),
                pub("Guitar World", MAG, "2019-12-31", "10456295", details("January 2020", null)),
                pub("Guitar World", MAG, "2020-02-29", "10456295", details("March 2020", null)),
                pub("Guitar World", MAG, "2021-05-01", "10456295", details("May 2021", null)));
    }

    @Test
    void pubSearchWithSearchNameMatchesPublicationIndexNameCaseInsensitively() {
        final var results = graphQlTester.document("""
                        query {
                            pubSearch(criteria: { searchName: "practicing" }) {
                                name
                                pubDate
                            }
                        }
                        """)
                .execute()
                .path("pubSearch")
                .entityList(PubSummaryResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubSummary("Guitar For The Practicing Musician", "1988-12-01"));
    }

    @Test
    void pubSearchWithSearchNameMatchesIssueNameCaseInsensitively() {
        final var results = graphQlTester.document("""
                        query {
                            pubSearch(criteria: { searchName: "holiday" }) {
                                name
                                pubDate
                            }
                        }
                        """)
                .execute()
                .path("pubSearch")
                .entityList(PubSummaryResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubSummary("Guitar World", "2018-12-01"));
    }

    @Test
    void pubSearchWithSearchNameMatchesBookEditionCaseInsensitively() {
        final var results = graphQlTester.document("""
                        query {
                            pubSearch(criteria: { searchName: "first printing" }) {
                                name
                                pubDate
                            }
                        }
                        """)
                .execute()
                .path("pubSearch")
                .entityList(PubSummaryResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubSummary("Tom Petty & the Heartbreakers: Greatest Hits", "1994-04-01"));
    }

    @Test
    void pubSearchWithBookTypeReturnsBookPublications() {
        final var results = graphQlTester.document("""
                        query {
                            pubSearch(criteria: { type: BOOK }) {
                                name
                                type
                                pubDate
                            }
                        }
                        """)
                .execute()
                .path("pubSearch")
                .entityList(PubTypeResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubType("Tom Petty & the Heartbreakers: Greatest Hits", BOOK, "1994-04-01"));
    }

    @Test
    void pubSearchWithMagazineTypeReturnsMagazinePublications() {
        final var results = graphQlTester.document("""
                        query {
                            pubSearch(criteria: { type: MAG }) {
                                name
                                type
                                pubDate
                            }
                        }
                        """)
                .execute()
                .path("pubSearch")
                .entityList(PubTypeResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubType("Guitar For The Practicing Musician", MAG, "1988-12-01"),
                pubType("Guitar World", MAG, "2018-11-01"),
                pubType("Guitar World", MAG, "2018-12-01"),
                pubType("Guitar World", MAG, "2019-12-31"),
                pubType("Guitar World", MAG, "2020-02-29"),
                pubType("Guitar World", MAG, "2021-05-01"));
    }

    @Test
    void pubSearchWithDateStartReturnsPublicationsOnOrAfterDate() {
        final var results = graphQlTester.document("""
                        query {
                            pubSearch(criteria: { dateStart: "2019-12-31" }) {
                                name
                                pubDate
                            }
                        }
                        """)
                .execute()
                .path("pubSearch")
                .entityList(PubSummaryResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubSummary("Guitar World", "2019-12-31"),
                pubSummary("Guitar World", "2020-02-29"),
                pubSummary("Guitar World", "2021-05-01"));
    }

    @Test
    void pubSearchWithDateEndReturnsPublicationsOnOrBeforeDate() {
        final var results = graphQlTester.document("""
                        query {
                            pubSearch(criteria: { dateEnd: "1994-04-01" }) {
                                name
                                pubDate
                            }
                        }
                        """)
                .execute()
                .path("pubSearch")
                .entityList(PubSummaryResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubSummary("Guitar For The Practicing Musician", "1988-12-01"),
                pubSummary("Tom Petty & the Heartbreakers: Greatest Hits", "1994-04-01"));
    }

    @Test
    void pubSearchWithDateStartAndDateEndReturnsPublicationsWithinInclusiveRange() {
        final var results = graphQlTester.document("""
                        query {
                            pubSearch(criteria: { dateStart: "2018-11-01", dateEnd: "2018-12-01" }) {
                                name
                                pubDate
                            }
                        }
                        """)
                .execute()
                .path("pubSearch")
                .entityList(PubSummaryResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubSummary("Guitar World", "2018-11-01"),
                pubSummary("Guitar World", "2018-12-01"));
    }

    @Test
    void pubSearchWithHasTranscriptionsReturnsOnlyPublicationsWithTranscriptions() {
        final var results = graphQlTester.document("""
                        query {
                            pubSearch(criteria: { hasTranscriptions: true }) {
                                name
                                pubDate
                            }
                        }
                        """)
                .execute()
                .path("pubSearch")
                .entityList(PubSummaryResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubSummary("Guitar For The Practicing Musician", "1988-12-01"),
                pubSummary("Tom Petty & the Heartbreakers: Greatest Hits", "1994-04-01"),
                pubSummary("Guitar World", "2018-11-01"),
                pubSummary("Guitar World", "2018-12-01"),
                pubSummary("Guitar World", "2021-05-01"));
    }

    @Test
    void pubSearchCombinesCriteriaElements() {
        final var results = graphQlTester.document("""
                        query {
                            pubSearch(criteria: {
                                searchName: "guitar world",
                                type: MAG,
                                dateStart: "2020-01-01",
                                hasTranscriptions: true
                            }) {
                                name
                                pubDate
                            }
                        }
                        """)
                .execute()
                .path("pubSearch")
                .entityList(PubSummaryResponse.class)
                .get();

        assertThat(results).containsExactly(
                pubSummary("Guitar World", "2021-05-01"));
    }

    private static PubResponse pub(
            final String name,
            final PubType type,
            final String pubDate,
            final String serialNumber,
            final DetailsResponse details) {

        return new PubResponse(
                name,
                type,
                LocalDate.parse(pubDate),
                serialNumber,
                details);
    }

    private static DetailsResponse details(final String issueName, final String edition) {
        return new DetailsResponse(issueName, edition);
    }

    private static PubSummaryResponse pubSummary(final String name, final String pubDate) {
        return new PubSummaryResponse(name, LocalDate.parse(pubDate));
    }

    private static PubTypeResponse pubType(final String name, final PubType type, final String pubDate) {
        return new PubTypeResponse(name, type, LocalDate.parse(pubDate));
    }

    private record PubResponse(
            String name,
            PubType type,
            LocalDate pubDate,
            String serialNumber,
            DetailsResponse details) {
    }

    private record DetailsResponse(String issueName, String edition) {
    }

    private record PubSummaryResponse(String name, LocalDate pubDate) {
    }

    private record PubTypeResponse(String name, PubType type, LocalDate pubDate) {
    }
}
