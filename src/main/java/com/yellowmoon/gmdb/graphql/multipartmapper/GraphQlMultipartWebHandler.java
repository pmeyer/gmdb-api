package com.yellowmoon.gmdb.graphql.multipartmapper;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_GRAPHQL_RESPONSE;

@Service
@RequiredArgsConstructor
public class GraphQlMultipartWebHandler {
    @SuppressWarnings("removal")
    public static final List<MediaType> SUPPORTED_MEDIA_TYPES =
            Arrays.asList(APPLICATION_GRAPHQL_RESPONSE, MediaType.APPLICATION_JSON, MediaType.APPLICATION_GRAPHQL);

    private final GraphQlMultipartRequestExtractor multipartRequestExtractor;
    private final WebGraphQlHandler webGraphQlHandler;

    public Mono<ServerResponse> handleGraphQlMultipartRequest(final ServerRequest serverRequest) {
        return serverRequest.multipartData()
                .flatMap(multipartRequestExtractor::extract)
                .flatMap(gqlMpReq -> {
                    final WebGraphQlRequest graphQlRequest = new WebGraphQlRequest(serverRequest.uri(),
                            serverRequest.headers().asHttpHeaders(),
                            serverRequest.cookies(),
                            serverRequest.remoteAddress().orElse(null),
                            serverRequest.attributes(),
                            gqlMpReq.toGraphQlRequest(),
                            serverRequest.exchange().getRequest().getId(),
                            serverRequest.exchange().getLocaleContext().getLocale());

                    return webGraphQlHandler.handleRequest(graphQlRequest);
                })
                .flatMap(response -> buildServerResponse(response, serverRequest.headers().accept()));
    }

    protected static Mono<ServerResponse> buildServerResponse(final WebGraphQlResponse response, final List<MediaType> acceptableMediaTypes) {
        final MediaType responseContentType = Optional.ofNullable(acceptableMediaTypes)
                .flatMap(t -> t.stream()
                        .filter(SUPPORTED_MEDIA_TYPES::contains)
                        .findFirst())
                .orElse(MediaType.APPLICATION_JSON);

        return ServerResponse.ok()
                .headers(headers -> headers.putAll(response.getResponseHeaders()))
                .contentType(responseContentType)
                .bodyValue(response.toMap());
    }
}
