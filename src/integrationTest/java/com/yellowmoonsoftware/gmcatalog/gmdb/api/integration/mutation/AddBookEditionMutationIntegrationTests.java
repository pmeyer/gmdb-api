package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.mutation;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;

import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType.BOOK;
import static org.assertj.core.api.Assertions.assertThat;

class AddBookEditionMutationIntegrationTests extends GmdbGraphQlMutationIntegrationTestSupport {

    private static final GmdbIntegrationDatabase DATABASE = createStartedMutationDatabase();

    @Autowired
    private WebGraphQlTester graphQlTester;

    @DynamicPropertySource
    static void registerGmdbIntegrationProperties(final DynamicPropertyRegistry registry) {
        registerMutationIntegrationProperties(registry, DATABASE);
    }

    @Test
    void addBookEditionWithExistingIndexCreatesPublicationForIndex() {
        final long pubIndexId = pubIndexIdBySerialNumber("0898987660");

        final var result = addBookEdition("""
                pubDate: "2025-03-01"
                index: { id: %d }
                info: {
                    edition: "Mutation Existing Book Index Edition"
                }
                transcriptions: []
                """.formatted(pubIndexId));

        assertThat(result.id()).isPositive();
        assertThat(result.name()).isEqualTo("Tom Petty & the Heartbreakers: Greatest Hits");
        assertThat(result.type()).isEqualTo(BOOK);
        assertThat(result.pubDate()).isEqualTo(LocalDate.of(2025, 3, 1));
        assertThat(result.serialNumber()).isEqualTo("0898987660");
        assertThat(result.pubIndexId()).isEqualTo(pubIndexId);
        assertThat(result.details()).isEqualTo(new BookDetailsResponse("Mutation Existing Book Index Edition"));
        assertThat(countBookEditions(
                pubIndexId,
                LocalDate.of(2025, 3, 1),
                "Mutation Existing Book Index Edition")).isOne();
    }

    @Test
    void addBookEditionWithNewIndexDataCreatesPublicationIndexAndPublication() {
        final var result = addBookEdition("""
                pubDate: "2025-04-01"
                index: {
                    data: {
                        name: "Mutation Test Songbook"
                        type: BOOK
                        serial: "MUT-BOOK-EDITION-001"
                    }
                }
                info: {
                    edition: "Mutation New Book Index Edition"
                }
                transcriptions: []
                """);

        assertThat(result.id()).isPositive();
        assertThat(result.name()).isEqualTo("Mutation Test Songbook");
        assertThat(result.type()).isEqualTo(BOOK);
        assertThat(result.pubDate()).isEqualTo(LocalDate.of(2025, 4, 1));
        assertThat(result.serialNumber()).isEqualTo("MUT-BOOK-EDITION-001");
        assertThat(result.pubIndexId()).isPositive();
        assertThat(result.details()).isEqualTo(new BookDetailsResponse("Mutation New Book Index Edition"));
        assertThat(countPubIndices("Mutation Test Songbook", BOOK, "MUT-BOOK-EDITION-001")).isOne();
        assertThat(countBookEditions(
                result.pubIndexId(),
                LocalDate.of(2025, 4, 1),
                "Mutation New Book Index Edition")).isOne();
    }

    @Test
    void addBookEditionWithOmittedOptionalFieldsCreatesPublication() {
        final long pubIndexId = pubIndexIdBySerialNumber("0898987660");

        final var result = addBookEdition("""
                pubDate: "2025-07-01"
                index: { id: %d }
                """.formatted(pubIndexId));

        assertThat(result.id()).isPositive();
        assertThat(result.name()).isEqualTo("Tom Petty & the Heartbreakers: Greatest Hits");
        assertThat(result.type()).isEqualTo(BOOK);
        assertThat(result.pubDate()).isEqualTo(LocalDate.of(2025, 7, 1));
        assertThat(result.serialNumber()).isEqualTo("0898987660");
        assertThat(result.pubIndexId()).isEqualTo(pubIndexId);
        assertThat(result.details()).isEqualTo(new BookDetailsResponse(null));
        assertThat(countBookEditionsWithNullEdition(pubIndexId, LocalDate.of(2025, 7, 1))).isOne();
    }

    @Test
    void addBookEditionWithOmittedPublicationDateCreatesPublication() {
        final long pubIndexId = pubIndexIdBySerialNumber("0898987660");

        final var result = addBookEdition("""
                index: { id: %d }
                """.formatted(pubIndexId));

        assertThat(result.id()).isPositive();
        assertThat(result.name()).isEqualTo("Tom Petty & the Heartbreakers: Greatest Hits");
        assertThat(result.type()).isEqualTo(BOOK);
        assertThat(result.pubDate()).isNull();
        assertThat(result.serialNumber()).isEqualTo("0898987660");
        assertThat(result.pubIndexId()).isEqualTo(pubIndexId);
        assertThat(result.details()).isEqualTo(new BookDetailsResponse(null));
        assertThat(countBookEditionsWithNullPublicationDateAndNullEdition(pubIndexId)).isOne();
    }

    @Test
    void addBookEditionWithExistingIndexIdAndDataUpdatesPublicationIndex() {
        addBookEdition("""
                pubDate: "2025-08-01"
                index: {
                    data: {
                        name: "Mutation Test Book Index Update Target"
                        type: BOOK
                        serial: "MUT-BOOK-ID-DATA-001"
                    }
                }
                info: {
                    edition: "Mutation Test Book Index Update First Edition"
                }
                """);

        final long pubIndexId = pubIndexIdBySerialNumber("MUT-BOOK-ID-DATA-001");

        final var result = addBookEdition("""
                pubDate: "2025-09-01"
                index: {
                    id: %d
                    data: {
                        name: "Mutation Test Book Index Updated"
                        type: BOOK
                        serial: "MUT-BOOK-ID-DATA-UPDATED"
                    }
                }
                info: {
                    edition: "Mutation Test Book Index Update Second Edition"
                }
                """.formatted(pubIndexId));

        assertThat(result.id()).isPositive();
        assertThat(result.name()).isEqualTo("Mutation Test Book Index Updated");
        assertThat(result.type()).isEqualTo(BOOK);
        assertThat(result.pubDate()).isEqualTo(LocalDate.of(2025, 9, 1));
        assertThat(result.serialNumber()).isEqualTo("MUT-BOOK-ID-DATA-UPDATED");
        assertThat(result.pubIndexId()).isEqualTo(pubIndexId);
        assertThat(result.details()).isEqualTo(new BookDetailsResponse(
                "Mutation Test Book Index Update Second Edition"));
        assertThat(countPubIndices("Mutation Test Book Index Update Target", BOOK, "MUT-BOOK-ID-DATA-001"))
                .isZero();
        assertThat(countPubIndices("Mutation Test Book Index Updated", BOOK, "MUT-BOOK-ID-DATA-UPDATED"))
                .isOne();
    }

    @Test
    void addBookEditionWithNestedTranscriptionCreatesPublicationAndTranscription() {
        final long pubIndexId = pubIndexIdBySerialNumber("0898987660");
        final long songId = songIdByTitleAndAlbum("Learning to Fly", "Greatest Hits");
        final long transcriberId = transcriberIdByName("Danny Begelman");

        final var result = addBookEdition("""
                pubDate: "2025-06-01"
                index: { id: %d }
                info: {
                    edition: "Mutation Book Edition With Transcription"
                }
                transcriptions: [{
                    song: { id: %d }
                    pageNumber: 21
                    transcribers: [{ id: %d }]
                }]
                """.formatted(pubIndexId, songId, transcriberId));

        assertThat(result.id()).isPositive();
        assertThat(result.pubIndexId()).isEqualTo(pubIndexId);
        assertThat(countBookEditions(
                pubIndexId,
                LocalDate.of(2025, 6, 1),
                "Mutation Book Edition With Transcription")).isOne();
        assertThat(countTranscriptions(songId, result.id(), 21)).isOne();
        assertThat(countTranscriptionTranscribers(songId, result.id(), transcriberId)).isOne();
    }

    private BookPubResponse addBookEdition(final String inputFields) {
        return graphQlTester.document("""
                        mutation {
                            addBookEdition(bookInput: {
                                %s
                            }) {
                                id
                                name
                                type
                                pubDate
                                serialNumber
                                pubIndexId
                                details {
                                    ... on BookDetails {
                                        edition
                                    }
                                }
                            }
                        }
                        """.formatted(inputFields))
                .execute()
                .path("addBookEdition")
                .entity(BookPubResponse.class)
                .get();
    }

    private static long pubIndexIdBySerialNumber(final String serialNumber) {
        return queryForLong(DATABASE, """
                select id
                from gmdb.pub_idx
                where serial_number = '%s'
                """.formatted(serialNumber));
    }

    private static long songIdByTitleAndAlbum(final String title, final String albumTitle) {
        return queryForLong(DATABASE, """
                select s.id
                from gmdb.song s
                    inner join gmdb.album a on s.album_id = a.id
                where s.title = '%s'
                    and a.title = '%s'
                """.formatted(title.replace("'", "''"), albumTitle.replace("'", "''")));
    }

    private static long transcriberIdByName(final String name) {
        return queryForLong(DATABASE, """
                select id
                from gmdb.transcriber
                where name = '%s'
                """.formatted(name.replace("'", "''")));
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

    private static int countBookEditions(
            final long pubIndexId,
            final LocalDate pubDate,
            final String edition) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub
                where pub_idx_id = %d
                    and pub_date = '%s'::date
                    and details->>'edition' = '%s'
                """.formatted(pubIndexId, pubDate, edition));
    }

    private static int countBookEditionsWithNullEdition(
            final long pubIndexId,
            final LocalDate pubDate) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub
                where pub_idx_id = %d
                    and pub_date = '%s'::date
                    and details->>'edition' is null
                """.formatted(pubIndexId, pubDate));
    }

    private static int countBookEditionsWithNullPublicationDateAndNullEdition(final long pubIndexId) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub
                where pub_idx_id = %d
                    and pub_date is null
                    and details->>'edition' is null
                """.formatted(pubIndexId));
    }

    private static int countTranscriptions(
            final long songId,
            final long pubId,
            final int pageNumber) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.transcription
                where song_id = %d
                    and pub_id = %d
                    and details->>'pageNumber' = '%d'
                """.formatted(songId, pubId, pageNumber));
    }

    private static int countTranscriptionTranscribers(
            final long songId,
            final long pubId,
            final long transcriberId) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.transcription t
                    inner join gmdb.transcription_transcriber tt on t.id = tt.transcription_id
                where t.song_id = %d
                    and t.pub_id = %d
                    and tt.transcriber_id = %d
                """.formatted(songId, pubId, transcriberId));
    }

    private record BookPubResponse(
            Long id,
            String name,
            PubType type,
            LocalDate pubDate,
            String serialNumber,
            Long pubIndexId,
            BookDetailsResponse details) {
    }

    private record BookDetailsResponse(String edition) {
    }
}
