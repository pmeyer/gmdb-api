package com.yellowmoon.gmdb.graphql.multipartmapper;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

@Accessors(fluent = true)
public class MockFilePart implements FilePart {
    @Getter
    private final String filename;

    @Getter
    private final String name;

    @Getter
    private final HttpHeaders headers;

    private final DataBuffer fileContent;

    public MockFilePart(final String filename, final String name, final String fileContent) {
        this(filename, name, fileContent.getBytes());
    }

    public MockFilePart(final String filename, final String name, final byte[] fileContent) {
        this.filename = filename;
        this.name = name;
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.TEXT_PLAIN);
        this.fileContent = DefaultDataBufferFactory.sharedInstance.wrap(fileContent);
    }

    @Override
    @NonNull
    public Mono<Void> transferTo(@NonNull final Path dest) {
        return Mono.empty();
    }

    @Override
    @NonNull
    public Flux<DataBuffer> content() {
        return Flux.defer(() -> Flux.just(fileContent));
    }
}
