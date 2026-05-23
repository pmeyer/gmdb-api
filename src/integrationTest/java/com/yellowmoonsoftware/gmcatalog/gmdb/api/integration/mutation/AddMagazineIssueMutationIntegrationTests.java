package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.mutation;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.Objects;

import static com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType.MAG;
import static org.assertj.core.api.Assertions.assertThat;

class AddMagazineIssueMutationIntegrationTests extends GmdbGraphQlMutationIntegrationTestSupport {
    private static final String COVER_IMAGE_UPLOAD_FILENAME = "gmdb-test-cover-image-upload.png";

    private static final GmdbIntegrationDatabase DATABASE = createStartedMutationDatabase();

    @Autowired
    private WebGraphQlTester graphQlTester;

    @Autowired
    private WebTestClient webTestClient;

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
    void addMagazineIssueWithOmittedOptionalFieldsCreatesPublication() {
        final long pubIndexId = pubIndexIdBySerialNumber("10456295");

        final var result = addMagazineIssue("""
                pubDate: "2025-07-15"
                index: { id: %d }
                info: {
                    issueName: "Mutation Test Magazine Optional Fields"
                }
                """.formatted(pubIndexId));

        assertThat(result.id()).isPositive();
        assertThat(result.name()).isEqualTo("Guitar World");
        assertThat(result.type()).isEqualTo(MAG);
        assertThat(result.pubDate()).isEqualTo(LocalDate.of(2025, 7, 15));
        assertThat(result.serialNumber()).isEqualTo("10456295");
        assertThat(result.pubIndexId()).isEqualTo(pubIndexId);
        assertThat(result.details()).isEqualTo(new MagDetailsResponse(
                null,
                null,
                "Mutation Test Magazine Optional Fields"));
        assertThat(countMagazineIssuesWithNullVolumeAndIssue(
                pubIndexId,
                LocalDate.of(2025, 7, 15),
                "Mutation Test Magazine Optional Fields")).isOne();
    }

    @Test
    void addMagazineIssueWithOmittedPublicationDateCreatesPublication() {
        final long pubIndexId = pubIndexIdBySerialNumber("10456295");

        final var result = addMagazineIssue("""
                index: { id: %d }
                info: {
                    issueName: "Mutation Test Magazine Without Publication Date"
                }
                """.formatted(pubIndexId));

        assertThat(result.id()).isPositive();
        assertThat(result.name()).isEqualTo("Guitar World");
        assertThat(result.type()).isEqualTo(MAG);
        assertThat(result.pubDate()).isNull();
        assertThat(result.serialNumber()).isEqualTo("10456295");
        assertThat(result.pubIndexId()).isEqualTo(pubIndexId);
        assertThat(result.details()).isEqualTo(new MagDetailsResponse(
                null,
                null,
                "Mutation Test Magazine Without Publication Date"));
        assertThat(countMagazineIssuesWithNullPublicationDateAndNullVolumeAndIssue(
                pubIndexId,
                "Mutation Test Magazine Without Publication Date")).isOne();
    }

    @Test
    void addMagazineIssueWithExistingIndexIdAndDataUpdatesPublicationIndex() {
        addMagazineIssue("""
                pubDate: "2025-08-15"
                index: {
                    data: {
                        name: "Mutation Test Magazine Index Update Target"
                        type: MAG
                        serial: "MUT-MAG-ID-DATA-001"
                    }
                }
                info: {
                    issueName: "Mutation Test Magazine Index Update First Issue"
                }
                """);

        final long pubIndexId = pubIndexIdBySerialNumber("MUT-MAG-ID-DATA-001");

        final var result = addMagazineIssue("""
                pubDate: "2025-09-15"
                index: {
                    id: %d
                    data: {
                        name: "Mutation Test Magazine Index Updated"
                        type: MAG
                        serial: "MUT-MAG-ID-DATA-UPDATED"
                    }
                }
                info: {
                    volume: "2"
                    issue: "9"
                    issueName: "Mutation Test Magazine Index Update Second Issue"
                }
                """.formatted(pubIndexId));

        assertThat(result.id()).isPositive();
        assertThat(result.name()).isEqualTo("Mutation Test Magazine Index Updated");
        assertThat(result.type()).isEqualTo(MAG);
        assertThat(result.pubDate()).isEqualTo(LocalDate.of(2025, 9, 15));
        assertThat(result.serialNumber()).isEqualTo("MUT-MAG-ID-DATA-UPDATED");
        assertThat(result.pubIndexId()).isEqualTo(pubIndexId);
        assertThat(result.details()).isEqualTo(new MagDetailsResponse(
                "2",
                "9",
                "Mutation Test Magazine Index Update Second Issue"));
        assertThat(countPubIndices("Mutation Test Magazine Index Update Target", MAG, "MUT-MAG-ID-DATA-001"))
                .isZero();
        assertThat(countPubIndices("Mutation Test Magazine Index Updated", MAG, "MUT-MAG-ID-DATA-UPDATED"))
                .isOne();
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

    @Test
    void addMagazineIssueStoresUploadedCoverImage() {
        final long pubIndexId = pubIndexIdBySerialNumber("10456295");

        final var result = executeAddMagazineIssueWithCoverImage(pubIndexId);

        assertThat(result.id()).isPositive();
        assertThat(result.name()).isEqualTo("Guitar World");
        assertThat(result.type()).isEqualTo(MAG);
        assertThat(result.pubDate()).isEqualTo(LocalDate.of(2025, 10, 15));
        assertThat(result.serialNumber()).isEqualTo("10456295");
        assertThat(result.pubIndexId()).isEqualTo(pubIndexId);
        assertThat(result.details()).satisfies(details -> {
            assertThat(details.volume()).isEqualTo("50");
            assertThat(details.issue()).isEqualTo("10");
            assertThat(details.issueName()).isEqualTo("Mutation Test Magazine Cover Upload");
            assertThat(details.cover()).contains(COVER_IMAGE_UPLOAD_FILENAME);
        });
        assertThat(countMagazineCoverImageResourcesWithUploadMetadata(result.id())).isOne();
        assertResourceResponseMatchesCoverImageUpload(result.details().cover());
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

    private MagCoverUploadPubResponse executeAddMagazineIssueWithCoverImage(final long pubIndexId) {
        final MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("operations", """
                {
                    "query": "mutation($magInput: MagazineInput!) { addMagazineIssue(magInput: $magInput) { id name type pubDate serialNumber pubIndexId details { ... on MagDetails { volume issue issueName cover } } } }",
                    "variables": {
                        "magInput": {
                            "pubDate": "2025-10-15",
                            "index": { "id": %d },
                            "info": {
                                "volume": "50",
                                "issue": "10",
                                "issueName": "Mutation Test Magazine Cover Upload",
                                "cover": null
                            },
                            "transcriptions": []
                        }
                    }
                }
                """.formatted(pubIndexId));
        multipartBodyBuilder.part("map", """
                {
                    "0": ["variables.magInput.info.cover"]
                }
                """);
        multipartBodyBuilder.part("0", new FileSystemResource(coverImageUploadPath()))
                .contentType(MediaType.IMAGE_PNG);

        final var response = webTestClient.mutate()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build()
                .post()
                .uri("/graphql")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MagCoverUploadGraphQlResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.data()).isNotNull();
        assertThat(response.data().addMagazineIssue()).isNotNull();
        return response.data().addMagazineIssue();
    }

    private void assertResourceResponseMatchesCoverImageUpload(final String resourceUrl) {
        assertThat(resourceUrl).isNotBlank();

        final byte[] responseBody = webTestClient.mutate()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build()
                .get()
                .uri(resourceUrl)
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(sha256(responseBody)).isEqualTo(sha256(readCoverImageUpload()));
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

    private static int countMagazineIssuesWithNullVolumeAndIssue(
            final long pubIndexId,
            final LocalDate pubDate,
            final String issueName) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub
                where pub_idx_id = %d
                    and pub_date = '%s'::date
                    and details->>'volume' is null
                    and details->>'issue' is null
                    and details->>'issueName' = '%s'
                """.formatted(pubIndexId, pubDate, issueName));
    }

    private static int countMagazineIssuesWithNullPublicationDateAndNullVolumeAndIssue(
            final long pubIndexId,
            final String issueName) {

        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub
                where pub_idx_id = %d
                    and pub_date is null
                    and details->>'volume' is null
                    and details->>'issue' is null
                    and details->>'issueName' = '%s'
                """.formatted(pubIndexId, issueName));
    }

    private static int countMagazineCoverImageResourcesWithUploadMetadata(final long pubId) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub
                where id = %d
                    and details->>'volume' = '50'
                    and details->>'issue' = '10'
                    and details->>'issueName' = 'Mutation Test Magazine Cover Upload'
                    and details->'resources'->'COVER_IMAGE'->>'originalFilename' = '%s'
                    and details->'resources'->'COVER_IMAGE'->>'mediaType' = 'image/png'
                """.formatted(pubId, COVER_IMAGE_UPLOAD_FILENAME));
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

    private static Path coverImageUploadPath() {
        try {
            return Path.of(Objects.requireNonNull(
                            AddMagazineIssueMutationIntegrationTests.class
                                    .getClassLoader()
                                    .getResource("test-upload-files/" + COVER_IMAGE_UPLOAD_FILENAME),
                            "Could not find cover image upload test resource")
                    .toURI());
        } catch (final URISyntaxException exception) {
            throw new IllegalStateException("Could not resolve cover image upload test resource", exception);
        }
    }

    private static byte[] readCoverImageUpload() {
        try {
            return Files.readAllBytes(coverImageUploadPath());
        } catch (final Exception exception) {
            throw new IllegalStateException("Could not read cover image upload test resource", exception);
        }
    }

    private static String sha256(final byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (final NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Could not create SHA-256 digest", exception);
        }
    }

    private record MagCoverUploadGraphQlResponse(MagCoverUploadGraphQlData data) {
    }

    private record MagCoverUploadGraphQlData(MagCoverUploadPubResponse addMagazineIssue) {
    }

    private record MagCoverUploadPubResponse(
            Long id,
            String name,
            PubType type,
            LocalDate pubDate,
            String serialNumber,
            Long pubIndexId,
            MagCoverUploadDetailsResponse details) {
    }

    private record MagCoverUploadDetailsResponse(String volume, String issue, String issueName, String cover) {
    }
}
