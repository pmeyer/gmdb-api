package com.yellowmoon.gmdb.graphql.multipartmapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

/// A Spring Component responsible for extracting GraphQL multipart requests from a [ServerRequest].
/// This class uses the [GraphQlMultipartDecoder] to decode multipart data, separating it into
/// GraphQL operations and file mappings as encapsulated in a [GraphQlMultipartRequest].
///
/// The `extract` method retrieves the multipart form data from the request, processes it into a
/// Map representation, and then asynchronously decodes the "operations" and "map" parts of the request.
/// The results are combined into a [GraphQlMultipartRequest] object, which represents both the
/// GraphQL operations and their associated file mappings.
///
/// Dependencies:
/// - [GraphQlMultipartDecoder]: An interface responsible for decoding specific parts of the multipart
///   request, such as the operations and file map, which are critical for handling multipart GraphQL requests
///   with file uploads.
///
/// Reactive Programming Model:
/// - This class makes use of Reactor's [Mono] type to enable non-blocking, asynchronous processing of
///   the request. It ensures efficient handling of potentially large multipart requests.
///
/// Key Features:
/// - Extracts and decodes GraphQL multipart requests from a [ServerRequest].
/// - Uses reactive programming to process the request asynchronously.
/// - Combines decoded operations and file map into a single [GraphQlMultipartRequest] object.
@Component
@RequiredArgsConstructor
public class GraphQlMultipartRequestExtractor  {
    private final GraphQlMultipartDecoder graphQlMultipartDecoder;

    public Mono<GraphQlMultipartRequest> extract(final MultiValueMap<String, Part> multipartData) {
        final Map<String, Part> partsMap = multipartData.toSingleValueMap();
        return graphQlMultipartDecoder.decodeOperations(partsMap)
                        .zipWith(graphQlMultipartDecoder.decodeFileMap(partsMap), GraphQlMultipartRequest::new);
    }
}
