package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.mutation;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;

import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType.MAG;
import static org.assertj.core.api.Assertions.assertThat;

class AddMagazineIssueMutationIntegrationTests extends GmdbGraphQlMutationIntegrationTestSupport {

    private static final GmdbIntegrationDatabase DATABASE = createStartedMutationDatabase();

    @Autowired
    private WebGraphQlTester graphQlTester;

    @DynamicPropertySource
    static void registerGmdbIntegrationProperties(final DynamicPropertyRegistry registry) {
        registerMutationIntegrationProperties(registry, DATABASE);
    }

    @Test
    void addMagazineIssueWithExistingIndexCreatesPublicationForIndex() {
        final long pubIndexId = pubIndexIdBySerialNumber("10456295");

        final var result = addMagazineIssue("""
                pubDate: "2025-01-15"
                index: { id: %d }
                info: {
                    volume: "50"
                    issue: "1"
                    issueName: "Mutation Test Existing Magazine Index"
                }
                transcriptions: []
                """.formatted(pubIndexId));

        assertThat(result.id()).isPositive();
        assertThat(result.name()).isEqualTo("Guitar World");
        assertThat(result.type()).isEqualTo(MAG);
        assertThat(result.pubDate()).isEqualTo(LocalDate.of(2025, 1, 15));
        assertThat(result.serialNumber()).isEqualTo("10456295");
        assertThat(result.pubIndexId()).isEqualTo(pubIndexId);
        assertThat(result.details()).isEqualTo(new MagDetailsResponse(
                "50",
                "1",
                "Mutation Test Existing Magazine Index"));
        assertThat(countMagazineIssues(
                pubIndexId,
                LocalDate.of(2025, 1, 15),
                "50",
                "1",
                "Mutation Test Existing Magazine Index")).isOne();
    }

    @Test
    void addMagazineIssueWithNewIndexDataCreatesPublicationIndexAndPublication() {
        final var result = addMagazineIssue("""
                pubDate: "2025-02-15"
                index: {
                    data: {
                        name: "Mutation Test Magazine"
                        type: MAG
                        serial: "MUT-MAG-ISSUE-001"
                    }
                }
                info: {
                    volume: "1"
                    issue: "2"
                    issueName: "Mutation Test New Magazine Index"
                }
                transcriptions: []
                """);

        assertThat(result.id()).isPositive();
        assertThat(result.name()).isEqualTo("Mutation Test Magazine");
        assertThat(result.type()).isEqualTo(MAG);
        assertThat(result.pubDate()).isEqualTo(LocalDate.of(2025, 2, 15));
        assertThat(result.serialNumber()).isEqualTo("MUT-MAG-ISSUE-001");
        assertThat(result.pubIndexId()).isPositive();
        assertThat(result.details()).isEqualTo(new MagDetailsResponse(
                "1",
                "2",
                "Mutation Test New Magazine Index"));
        assertThat(countPubIndices("Mutation Test Magazine", MAG, "MUT-MAG-ISSUE-001")).isOne();
        assertThat(countMagazineIssues(
                result.pubIndexId(),
                LocalDate.of(2025, 2, 15),
                "1",
                "2",
                "Mutation Test New Magazine Index")).isOne();
    }

    @Test
    void addMagazineIssueWithNestedTranscriptionCreatesPublicationAndTranscription() {
        final long pubIndexId = pubIndexIdBySerialNumber("10456295");
        final long songId = songIdByTitleAndAlbum("Substitute", "Meaty Beaty Big and Bouncy");
        final long transcriberId = transcriberIdByName("Dave Whitehill");

        final var result = addMagazineIssue("""
                pubDate: "2025-05-15"
                index: { id: %d }
                info: {
                    volume: "50"
                    issue: "5"
                    issueName: "Mutation Test Magazine With Transcription"
                }
                transcriptions: [{
                    song: { id: %d }
                    pageNumber: 72
                    transcribers: [{ id: %d }]
                }]
                """.formatted(pubIndexId, songId, transcriberId));

        assertThat(result.id()).isPositive();
        assertThat(result.pubIndexId()).isEqualTo(pubIndexId);
        assertThat(countMagazineIssues(
                pubIndexId,
                LocalDate.of(2025, 5, 15),
                "50",
                "5",
                "Mutation Test Magazine With Transcription")).isOne();
        assertThat(countTranscriptions(songId, result.id(), 72)).isOne();
        assertThat(countTranscriptionTranscribers(songId, result.id(), transcriberId)).isOne();
    }

    private MagPubResponse addMagazineIssue(final String inputFields) {
        return graphQlTester.document("""
                        mutation {
                            addMagazineIssue(magInput: {
                                %s
                            }) {
                                id
                                name
                                type
                                pubDate
                                serialNumber
                                pubIndexId
                                details {
                                    ... on MagDetails {
                                        volume
                                        issue
                                        issueName
                                    }
                                }
                            }
                        }
                        """.formatted(inputFields))
                .execute()
                .path("addMagazineIssue")
                .entity(MagPubResponse.class)
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

    private static int countMagazineIssues(
            final long pubIndexId,
            final LocalDate pubDate,
            final String volume,
            final String issue,
            final String issueName) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub
                where pub_idx_id = %d
                    and pub_date = '%s'::date
                    and details->>'volume' = '%s'
                    and details->>'issue' = '%s'
                    and details->>'issueName' = '%s'
                """.formatted(pubIndexId, pubDate, volume, issue, issueName));
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

    private record MagPubResponse(
            Long id,
            String name,
            PubType type,
            LocalDate pubDate,
            String serialNumber,
            Long pubIndexId,
            MagDetailsResponse details) {
    }

    private record MagDetailsResponse(String volume, String issue, String issueName) {
    }
}
