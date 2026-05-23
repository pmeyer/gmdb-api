package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.mutation;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
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

import static org.assertj.core.api.Assertions.assertThat;

class AddPubCoverImageMutationIntegrationTests extends GmdbGraphQlMutationIntegrationTestSupport {
    private static final String COVER_IMAGE_UPLOAD_FILENAME = "gmdb-test-cover-image-upload.png";

    private static final GmdbIntegrationDatabase DATABASE = createStartedMutationDatabase();

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void registerGmdbIntegrationProperties(final DynamicPropertyRegistry registry) {
        registerMutationIntegrationProperties(registry, DATABASE);
    }

    @Test
    void addPubCoverImageStoresUploadedCoverImageForPublicationWithoutExistingImage() {
        final long pubId = pubIdForGuitarWorldHoliday2018();
        assertThat(countCoverImageResources(pubId)).isZero();

        final var result = executeAddPubCoverImage(pubId);

        assertThat(result.id()).isEqualTo(pubId);
        assertThat(result.name()).isEqualTo("Guitar World");
        assertThat(result.type()).isEqualTo(PubType.MAG);
        assertThat(result.pubDate()).isEqualTo(LocalDate.of(2018, 12, 1));
        assertThat(result.serialNumber()).isEqualTo("10456295");
        assertThat(result.pubIndexId()).isPositive();
        assertThat(result.details()).satisfies(details -> {
            assertThat(details.volume()).isEqualTo("39");
            assertThat(details.issue()).isEqualTo("13");
            assertThat(details.issueName()).isEqualTo("Holiday 2018");
            assertThat(details.cover()).contains(COVER_IMAGE_UPLOAD_FILENAME);
        });

        assertThat(countCoverImageResourcesWithUploadMetadata(pubId)).isOne();
        assertResourceResponseMatchesUpload(result.details().cover());
    }

    private PubResponse executeAddPubCoverImage(final long pubId) {
        final MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("operations", """
                {
                    "query": "mutation($imgInput: PubCoverImageInput!) { addPubCoverImage(imgInput: $imgInput) { id name type pubDate serialNumber pubIndexId details { ... on MagDetails { volume issue issueName cover } } } }",
                    "variables": {
                        "imgInput": {
                            "id": %d,
                            "cover": null
                        }
                    }
                }
                """.formatted(pubId));
        multipartBodyBuilder.part("map", """
                {
                    "0": ["variables.imgInput.cover"]
                }
                """);
        multipartBodyBuilder.part("0", new FileSystemResource(uploadPath()))
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
                .expectBody(GraphQlResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.data()).isNotNull();
        assertThat(response.data().addPubCoverImage()).isNotNull();
        return response.data().addPubCoverImage();
    }

    private void assertResourceResponseMatchesUpload(final String resourceUrl) {
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
        assertThat(sha256(responseBody)).isEqualTo(sha256(readUpload()));
    }

    private static long pubIdForGuitarWorldHoliday2018() {
        return queryForLong(DATABASE, """
                select p.id
                from gmdb.pub p
                    inner join gmdb.pub_idx pi on p.pub_idx_id = pi.id
                where pi.name = 'Guitar World'
                    and p.details->>'volume' = '39'
                    and p.details->>'issue' = '13'
                    and p.details->>'issueName' = 'Holiday 2018'
                """);
    }

    private static int countCoverImageResources(final long pubId) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub
                where id = %d
                    and details->'resources'->'COVER_IMAGE' is not null
                """.formatted(pubId));
    }

    private static int countCoverImageResourcesWithUploadMetadata(final long pubId) {
        return queryForInt(DATABASE, """
                select count(*)
                from gmdb.pub
                where id = %d
                    and details->>'volume' = '39'
                    and details->>'issue' = '13'
                    and details->>'issueName' = 'Holiday 2018'
                    and details->'resources'->'COVER_IMAGE'->>'originalFilename' = '%s'
                    and details->'resources'->'COVER_IMAGE'->>'mediaType' = 'image/png'
                """.formatted(pubId, COVER_IMAGE_UPLOAD_FILENAME));
    }

    private static Path uploadPath() {
        try {
            return Path.of(Objects.requireNonNull(
                            AddPubCoverImageMutationIntegrationTests.class
                                    .getClassLoader()
                                    .getResource("test-upload-files/" + COVER_IMAGE_UPLOAD_FILENAME),
                            "Could not find upload test resource")
                    .toURI());
        } catch (final URISyntaxException exception) {
            throw new IllegalStateException("Could not resolve upload test resource", exception);
        }
    }

    private static byte[] readUpload() {
        try {
            return Files.readAllBytes(uploadPath());
        } catch (final Exception exception) {
            throw new IllegalStateException("Could not read upload test resource", exception);
        }
    }

    private static String sha256(final byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (final NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Could not create SHA-256 digest", exception);
        }
    }

    private record GraphQlResponse(GraphQlData data) {
    }

    private record GraphQlData(PubResponse addPubCoverImage) {
    }

    private record PubResponse(
            Long id,
            String name,
            PubType type,
            LocalDate pubDate,
            String serialNumber,
            Long pubIndexId,
            MagDetailsResponse details) {
    }

    private record MagDetailsResponse(String volume, String issue, String issueName, String cover) {
    }
}
