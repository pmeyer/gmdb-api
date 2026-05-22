package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.mutation;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.NoSuchElementException;

import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType.BOOK;
import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType.MAG;
import static org.assertj.core.api.Assertions.assertThat;

class UpsertPubIndexMutationIntegrationTests extends GmdbGraphQlMutationIntegrationTestSupport {

    private static final GmdbIntegrationDatabase DATABASE = createStartedMutationDatabase();

    @Autowired
    private WebGraphQlTester graphQlTester;

    @DynamicPropertySource
    static void registerGmdbIntegrationProperties(final DynamicPropertyRegistry registry) {
        registerMutationIntegrationProperties(registry, DATABASE);
    }

    @Test
    void upsertPubIndexWithDataCreatesNewPublicationIndex() {
        final var result = upsertPubIndex("""
                data: {
                    name: "Mutation Test Monthly"
                    type: MAG
                    serial: "MUT-MAG-001"
                }
                """);

        assertThat(result.id()).isPositive();
        assertThat(result).isEqualTo(pubIndex(
                result.id(),
                "Mutation Test Monthly",
                MAG,
                "MUT-MAG-001"));
        assertThat(countPubIndices("Mutation Test Monthly", MAG, "MUT-MAG-001")).isOne();
    }

    @Test
    void upsertPubIndexWithIdOnlyReturnsExistingPublicationIndex() {
        final var existing = getPubIndexBySerialNumber("10456295");

        final var result = upsertPubIndex("id: %d".formatted(existing.id()));

        assertThat(result).isEqualTo(existing);
    }

    @Test
    void upsertPubIndexWithExistingIdAndDataUpdatesPublicationIndex() {
        final var existing = getPubIndexBySerialNumber("0898987660");

        final var result = upsertPubIndex("""
                id: %d
                data: {
                    name: "Tom Petty Anthology Mutation Test"
                    type: BOOK
                    serial: "MUT-BOOK-UPDATE-001"
                }
                """.formatted(existing.id()));

        assertThat(result).isEqualTo(pubIndex(
                existing.id(),
                "Tom Petty Anthology Mutation Test",
                BOOK,
                "MUT-BOOK-UPDATE-001"));
        assertThat(countPubIndices("Tom Petty Anthology Mutation Test", BOOK, "MUT-BOOK-UPDATE-001")).isOne();
        assertThat(countPubIndices(
                "Tom Petty & the Heartbreakers: Greatest Hits",
                BOOK,
                "0898987660")).isZero();
    }

    @Test
    void upsertPubIndexWithMatchingNameAndTypeUpdatesPublicationIndex() {
        final var existing = getPubIndexBySerialNumber("0738937X");

        final var result = upsertPubIndex("""
                data: {
                    name: "Guitar For The Practicing Musician"
                    type: MAG
                    serial: "MUT-MAG-NATURAL-001"
                }
                """);

        assertThat(result).isEqualTo(pubIndex(
                existing.id(),
                "Guitar For The Practicing Musician",
                MAG,
                "MUT-MAG-NATURAL-001"));
        assertThat(countPubIndices("Guitar For The Practicing Musician", MAG, "MUT-MAG-NATURAL-001")).isOne();
        assertThat(countPubIndices("Guitar For The Practicing Musician", MAG, "0738937X")).isZero();
    }

    private PubIndexResponse upsertPubIndex(final String inputFields) {
        return graphQlTester.document("""
                        mutation {
                            upsertPubIndex(pubIndexInput: {
                                %s
                            }) {
                                id
                                name
                                type
                                serialNumber
                            }
                        }
                        """.formatted(inputFields))
                .execute()
                .path("upsertPubIndex")
                .entity(PubIndexResponse.class)
                .get();
    }

    private PubIndexResponse getPubIndexBySerialNumber(final String serialNumber) {
        return graphQlTester.document("""
                        query {
                            getPubIndices {
                                id
                                name
                                type
                                serialNumber
                            }
                        }
                        """)
                .execute()
                .path("getPubIndices")
                .entityList(PubIndexResponse.class)
                .get()
                .stream()
                .filter(pubIndex -> serialNumber.equals(pubIndex.serialNumber()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Could not find pub index " + serialNumber));
    }

    private static int countPubIndices(
            final String name,
            final PubType type,
            final String serialNumber) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub_idx
                where name = '%s'
                    and type = '%s'::pub_type
                    and serial_number = '%s'
                """.formatted(name, type, serialNumber));
    }

    private static PubIndexResponse pubIndex(
            final Long id,
            final String name,
            final PubType type,
            final String serialNumber) {

        return new PubIndexResponse(id, name, type, serialNumber);
    }

    private record PubIndexResponse(Long id, String name, PubType type, String serialNumber) {
    }
}
