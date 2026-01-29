package com.yellowmoonsoftware.gmcatalog.gmdb.api;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

public class TestUtilities {

    static public FilePart fakeFilePart(final String name, final String contentType) {
        return fakeFilePart(name, MediaType.valueOf(contentType));
    }

    static public FilePart fakeFilePart(final String name, final MediaType contentType) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);

        return new FilePart() {
            @NonNull
            @Override
            public String filename() {
                return name;
            }

            @NonNull
            @Override
            public Mono<Void> transferTo(@NonNull final Path dest) {
                return Mono.empty();
            }

            @NonNull
            @Override
            public String name() {
                return name;
            }

            @NonNull
            @Override
            public HttpHeaders headers() {
                return headers;
            }

            @NonNull
            @Override
            public Flux<DataBuffer> content() {
                return Flux.empty();
            }
        };
    }
}
