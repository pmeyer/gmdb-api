package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;

import static org.assertj.core.api.Assertions.assertThat;

class QueryValidationIntegrationTests extends GmdbGraphQlQueryIntegrationTestSupport {

    @Autowired
    private WebGraphQlTester graphQlTester;

    @Test
    void pubSearchRejectsInvalidDateLiteral() {
        assertRequestError("""
                query {
                    pubSearch(criteria: { dateStart: "not-a-date" }) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void albumSearchRejectsInvalidOrderByColumn() {
        assertRequestError("""
                query {
                    albumSearch(criteria: { orderBy: [{ column: NAME }] }) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void songSearchRejectsArtistCriteriaWithoutRequiredArtistId() {
        assertRequestError("""
                query {
                    songSearch(criteria: { artists: [{ roles: [ALBUM_ARTIST] }] }) {
                        id
                    }
                }
                """, "Validation error");
    }

    @Test
    void getPubIndicesRejectsStringLiteralForPublicationType() {
        assertRequestError("""
                query {
                    getPubIndices(criteria: { type: "BOOK" }) {
                        id
                    }
                }
                """, "Validation error");
    }

    private void assertRequestError(final String document, final String message) {
        graphQlTester.document(document)
                .execute()
                .errors()
                .satisfy(errors -> assertThat(errors)
                        .anySatisfy(error -> assertThat(error.getMessage()).contains(message)));
    }
}
