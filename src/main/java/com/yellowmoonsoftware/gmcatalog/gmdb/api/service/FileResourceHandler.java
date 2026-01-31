package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FileResourceHandler {

    private final FileService fileService;

    public Mono<ServerResponse> getFile(final ServerRequest request) {
        final ServerResponse.BodyBuilder builder = ServerResponse.ok();

        request.queryParam("_f")
                .ifPresent(f -> {
                    builder.headers(h -> h.setContentDisposition(ContentDisposition
                        .builder("inline")
                        .filename(f)
                        .build()));

                    MediaTypeFactory.getMediaType(f)
                            .ifPresent(builder::contentType);
                });

        request.queryParam("_mt")
                .ifPresent(mt -> {
                    builder.contentType(MediaType.parseMediaType(mt));
                });
        return builder
                .body(BodyInserters
                .fromDataBuffers(fileService.get(ResourceSlug.getResourceSlugByName(request.pathVariable("slug")), request.pathVariables())));
    }
}
