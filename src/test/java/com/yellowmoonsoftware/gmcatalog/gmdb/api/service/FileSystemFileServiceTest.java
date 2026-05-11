package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class FileSystemFileServiceTest {

    @TempDir
    private Path tempDir;

    @Test
    void putCreatesParentDirectoriesTransfersFileAndReturnsResourceReference() {
        final FileSystemFileService service = new FileSystemFileService(tempDir.toString(), "");
        final UUID resourceId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final TestFilePart filePart = new TestFilePart("cover", "cover.jpg", "image bytes");

        StepVerifier.create(service.put(filePart, ResourceSlug.COVER_IMAGE, Map.of("id", resourceId)))
            .assertNext(reference -> {
                assertThat(reference.resourceSlug()).isEqualTo(ResourceSlug.COVER_IMAGE);
                assertThat(reference.slugPath()).isEqualTo("pub/%s/cover-img".formatted(resourceId));
                assertThat(reference.originalName()).isEqualTo("cover.jpg");
            })
            .verifyComplete();

        final Path expectedPath = tempDir.resolve("pub").resolve(resourceId.toString()).resolve("cover-img");
        assertThat(expectedPath).hasContent("image bytes");
        assertThat(filePart.transferredTo()).isEqualTo(expectedPath);
    }

    @Test
    void putExpandsTildeRootAgainstUserHome() {
        final FileSystemFileService service = new FileSystemFileService("~" + File.separator + "gmdb-files", tempDir.toString());
        final UUID resourceId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final TestFilePart filePart = new TestFilePart("album-art", "art.jpg", "album art");

        StepVerifier.create(service.put(filePart, ResourceSlug.ALBUM_ART, Map.of("id", resourceId)))
            .assertNext(reference -> assertThat(reference.slugPath()).isEqualTo("album/%s/album-art".formatted(resourceId)))
            .verifyComplete();

        final Path expectedPath = tempDir.resolve("gmdb-files").resolve("album").resolve(resourceId.toString()).resolve("album-art");
        assertThat(expectedPath).hasContent("album art");
    }

    @Test
    void getReadsExistingFileAsDataBuffers() throws IOException {
        final FileSystemFileService service = new FileSystemFileService(tempDir.toString(), "");
        final UUID resourceId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        final Path path = tempDir.resolve("transcription").resolve(resourceId.toString()).resolve("transcription");
        Files.createDirectories(path.getParent());
        Files.writeString(path, "transcription text", StandardCharsets.UTF_8);

        StepVerifier.create(DataBufferUtils.join(service.get(ResourceSlug.TRANSCRIPTION, Map.of("id", resourceId))))
            .assertNext(buffer -> {
                try {
                    assertThat(readString(buffer)).isEqualTo("transcription text");
                } finally {
                    DataBufferUtils.release(buffer);
                }
            })
            .verifyComplete();
    }

    @Test
    void getFailsWhenPathDoesNotResolveToRegularFile() {
        final FileSystemFileService service = new FileSystemFileService(tempDir.toString(), "");
        final UUID resourceId = UUID.fromString("00000000-0000-0000-0000-000000000004");

        StepVerifier.create(service.get(ResourceSlug.COVER_IMAGE, Map.of("id", resourceId)))
            .expectErrorSatisfies(error -> assertThat(error)
                .isInstanceOf(FileNotFoundException.class)
                .hasMessageContaining("pub/%s/cover-img".formatted(resourceId)))
            .verify();
    }

    private static String readString(DataBuffer buffer) {
        final byte[] bytes = new byte[buffer.readableByteCount()];
        buffer.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static class TestFilePart implements FilePart {
        private final String name;
        private final String filename;
        private final byte[] content;
        private final AtomicReference<Path> transferredTo = new AtomicReference<>();

        private TestFilePart(String name, String filename, String content) {
            this.name = name;
            this.filename = filename;
            this.content = content.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public String filename() {
            return filename;
        }

        @Override
        public Mono<Void> transferTo(Path dest) {
            return Mono.fromRunnable(() -> {
                try {
                    transferredTo.set(dest);
                    Files.write(dest, content);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public HttpHeaders headers() {
            return HttpHeaders.EMPTY;
        }

        @Override
        public Flux<DataBuffer> content() {
            return Flux.just(new DefaultDataBufferFactory().wrap(content));
        }

        private Path transferredTo() {
            return transferredTo.get();
        }
    }
}
