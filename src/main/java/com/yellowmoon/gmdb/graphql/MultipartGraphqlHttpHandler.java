package com.yellowmoon.gmdb.graphql;

import graphql.com.google.common.collect.Maps;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_GRAPHQL_RESPONSE;

/// The MultipartGraphqlHttpHandler class is responsible for handling multipart GraphQL
/// HTTP requests in a reactive programming context. It processes multipart request data,
/// deserializes parts into structured inputs, and invokes a GraphQL handler to process
/// the request.
///
/// Features:
/// - Supports selecting suitable response media types based on the request's "Accept" header.
/// - Handles multipart data with deserialization of specific parts like "operations" and "map".
/// - Manages variable mapping for multipart inputs, allowing complex GraphQL request structures.
/// - Prepares and invokes a GraphQL handler with the reconstructed request body.
///
/// Key Components:
/// - A [WebGraphQlHandler] instance to process GraphQL requests.
/// - A [Decoder] instance for deserialization of JSON parts from the request.
/// - Built-in handling of supported media types for response formatting.
///
/// Request Handling Flow:
/// 1. Extract multipart data from the incoming request.
/// 2. Deserialize specific parts into required structures such as "operations" and "map".
/// 3. Map file parts to variables based on the mapping information provided.
/// 4. Construct a GraphQL request object and pass it to the GraphQL handler.
/// 5. Prepare and return a server response based on the handler's output.
///
/// Utility Methods:
/// - `deserializePart`: Deserializes a specific part of the multipart request.
/// - `getOrDefault`: Safely retrieves a value from a map with a fallback default.
/// - `selectResponseMediaType`: Chooses an appropriate response media type from the request headers.
///
/// This class facilitates seamless handling of advanced GraphQL operations
/// that require multipart forms, such as file uploads combined with query operations.
public class MultipartGraphqlHttpHandler {
    @SuppressWarnings("removal")
    public static final List<MediaType> SUPPORTED_MEDIA_TYPES =
            Arrays.asList(APPLICATION_GRAPHQL_RESPONSE, MediaType.APPLICATION_JSON, MediaType.APPLICATION_GRAPHQL);

    private static final ParameterizedTypeReference<Map<String, List<String>>> LIST_PARAMETERIZED_TYPE_REF =
            new ParameterizedTypeReference<>() { };

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_PARAMETERIZED_TYPE_REF =
            new ParameterizedTypeReference<>() { };

    private final WebGraphQlHandler graphqlHandler;
    private final Decoder<?> jsonDecoder;

    public MultipartGraphqlHttpHandler(final WebGraphQlHandler graphqlHandler, final Decoder<?> jsonDecoder) {
        this.graphqlHandler = graphqlHandler;
        this.jsonDecoder = jsonDecoder;
    }

    public Mono<ServerResponse> handleMultipartRequest(ServerRequest serverRequest) {
        return serverRequest.multipartData().flatMap(mpData -> {
            final Map<String, Part> partsMap = mpData.toSingleValueMap();

            return Mono.zip(
                deserializePart(partsMap, "operations", Maps.newHashMap(), MAP_PARAMETERIZED_TYPE_REF),
                deserializePart(partsMap, "map", Maps.newHashMap(), LIST_PARAMETERIZED_TYPE_REF)
            ).flatMap(t -> {
                final Map<String, Object> opsInput = t.getT1();
                final Map<String, List<String>> fileMapInput = t.getT2();

                final Map<String, Object> variables = getOrDefault(opsInput, "variables", Maps.newHashMap());

                fileMapInput.forEach((key, paths) ->
                        Optional.ofNullable(partsMap.get(key))
                                .ifPresent(part -> {
                                    if (part instanceof FilePart filePart) {
                                        paths.forEach(path -> MultipartVariableMapper.mapVariable(path, variables, filePart));
                                    }
                        }));

                final String query = (String)opsInput.get("query");
                final String opName = (String)opsInput.get("operationName");
                final Map<String, Object> extensions = getOrDefault(opsInput, "extensions", Maps.newHashMap());

                final Map<String, Object> body = Map.of(
                        "query", query,
                        "operationName", StringUtils.hasText(opName) ? opName : "",
                        "variables", variables,
                        "extensions", extensions
                );

                final WebGraphQlRequest graphQlRequest = new WebGraphQlRequest(serverRequest.uri(),
                        serverRequest.headers().asHttpHeaders(),
                        serverRequest.cookies(),
                        serverRequest.remoteAddress().orElse(null),
                        serverRequest.attributes(),
                        body,
                        serverRequest.exchange().getRequest().getId(),
                        serverRequest.exchange().getLocaleContext().getLocale());

                return this.graphqlHandler.handleRequest(graphQlRequest);
            });
        }).flatMap(response -> {
            ServerResponse.BodyBuilder builder = ServerResponse.ok();
            builder.headers(headers -> headers.putAll(response.getResponseHeaders()));
            builder.contentType(selectResponseMediaType(serverRequest));
            return builder.bodyValue(response.toMap());
        });
    }

    @SuppressWarnings("unchecked")
    private <T> Mono<T> deserializePart(final Map<String, Part> partsMap, final String name, final T defaultValue, final ParameterizedTypeReference<T> type) {
        return Optional.ofNullable(partsMap.get(name))
                .map(part -> {
                    final Decoder<T> typedDecorder = (Decoder<T>) jsonDecoder;
                    return typedDecorder.decodeToMono(part.content(), ResolvableType.forType(type), MediaType.APPLICATION_JSON, null);
                })
                .orElse(Mono.just(defaultValue));
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOrDefault(final Map<String, Object> map, final String key, final T defaultValue) {
        return (T)map.getOrDefault(key, defaultValue);
    }

    private static MediaType selectResponseMediaType(ServerRequest serverRequest) {
        return serverRequest.headers().accept()
                .stream()
                .filter(SUPPORTED_MEDIA_TYPES::contains)
                .findFirst()
                .orElse(MediaType.APPLICATION_JSON);
    }
}
