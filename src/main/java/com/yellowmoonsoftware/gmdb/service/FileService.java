package com.yellowmoonsoftware.gmdb.service;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;


public interface FileService {
    Mono<ResourceReference> put(final FilePart filePart, final ResourceSlug slug, final Map<String, ?> slugVariables);

    Flux<DataBuffer> get(final ResourceSlug slug, final Map<String, ?> slugVariables);
}

