package com.yellowmoonsoftware.gmcatalog.gmdb.api.integration.query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceFileQueryIntegrationTests extends GmdbGraphQlQueryIntegrationTestSupport {
    private static final String ALBUM_RESOURCE_ID = "4b8c2c6f-0a74-4d5a-a271-f4734b6ce8a2";
    private static final String PUB_RESOURCE_ID = "9d4c6f61-2c7d-49d1-9e36-0e99afef0cf7";
    private static final String TRANSCRIPTION_RESOURCE_ID = "f8e2c95a-6f45-44cb-8a4f-2e7e33f3df70";

    @Autowired
    private WebGraphQlTester graphQlTester;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void songSearchResourceUrlsResolveToExpectedTestResources() {
        final var songs = graphQlTester.document("""
                        query {
                            songSearch(criteria: { titleSearch: "Rocket Queen" }) {
                                title
                                album {
                                    albumArtUrl
                                }
                                transcriptions {
                                    url
                                    pub {
                                        details {
                                            cover
                                        }
                                    }
                                }
                            }
                        }
                        """)
                .execute()
                .path("songSearch")
                .entityList(SongResourceResponse.class)
                .get();

        assertThat(songs).hasSize(1);
        final var song = songs.getFirst();
        assertThat(song.title()).isEqualTo("Rocket Queen");
        assertResourceResponseMatchesTestResource(
                song.album().albumArtUrl(),
                Path.of("album", ALBUM_RESOURCE_ID, "album-art"));

        assertThat(song.transcriptions()).hasSize(1);
        final TranscriptionResourceResponse transcription = song.transcriptions().getFirst();
        assertResourceResponseMatchesTestResource(
                transcription.url(),
                Path.of("transcription", TRANSCRIPTION_RESOURCE_ID, "transcription"));
        assertResourceResponseMatchesTestResource(
                transcription.pub().details().cover(),
                Path.of("pub", PUB_RESOURCE_ID, "cover-img"));
    }

    private void assertResourceResponseMatchesTestResource(final String resourceUrl, final Path testResourcePath) {
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
        assertThat(sha256(responseBody)).isEqualTo(sha256(readTestResource(testResourcePath)));
    }

    private static byte[] readTestResource(final Path testResourcePath) {
        try {
            return Files.readAllBytes(fileRepoRoot().resolve(testResourcePath));
        } catch (final Exception exception) {
            throw new IllegalStateException("Could not read test resource " + testResourcePath, exception);
        }
    }

    private static String sha256(final byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (final NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Could not create SHA-256 digest", exception);
        }
    }

    private record SongResourceResponse(
            String title,
            AlbumResourceResponse album,
            List<TranscriptionResourceResponse> transcriptions) {
    }

    private record AlbumResourceResponse(String albumArtUrl) {
    }

    private record TranscriptionResourceResponse(String url, TranscriptionPubResourceResponse pub) {
    }

    private record TranscriptionPubResourceResponse(PubDetailsResourceResponse details) {
    }

    private record PubDetailsResourceResponse(String cover) {
    }
}
