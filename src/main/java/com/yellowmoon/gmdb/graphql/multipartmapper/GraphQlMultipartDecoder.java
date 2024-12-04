package com.yellowmoon.gmdb.graphql.multipartmapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/// GraphQlMultipartDecoder is an interface designed to handle the decoding of multipart GraphQL requests,
/// specifically for multipart file upload operations. It provides methods to decode specific parts of the
/// request, such as the GraphQL operations map and file mapping.
/// The interface defines constants and parameterized type references that are used to facilitate the
/// decoding process. It also includes default methods to simplify common decoding patterns, such as
/// extracting the operations and file map from the multipart request.
///
/// Key Features:
/// - Decoding the "operations" part of a multipart request, which contains the GraphQL query or mutation.
/// - Decoding the "map" part of a multipart request to relate file parts to their corresponding variables.
/// - Uses `reactor.core.publisher.Mono` for reactive and asynchronous processing.
/// - Facilitates customization through the method `decodeMap`, which can be implemented to process
///   specific decoding logic.
///
/// Constants:
/// - OPERATIONS_KEY specifies the key used to identify the GraphQL operations part.
/// - MAP_KEY specifies the key used to identify the mappings of file parts.
/// - LIST_PARAMETERIZED_TYPE_REF and MAP_PARAMETERIZED_TYPE_REF are parameterized type references
///   for decoding JSON structures into Maps.
///
/// Default Methods:
/// - `decodeOperations`: Decodes the operations part of the multipart request.
/// - `decodeFileMap`: Decodes the map part of the request that maps file parts to variables.
///
/// The class is most commonly used for GraphQL servers that support file upload functionality using
/// multipart requests. By implementing this interface, users can define custom decoding behavior
/// tailored to their application's requirements.
public interface GraphQlMultipartDecoder {

    @Getter
    @RequiredArgsConstructor
    enum PartKey {
        OPERATIONS("operations", new ParameterizedTypeReference<Map<String, Object>>() { } ),
        MAP("map", new ParameterizedTypeReference<Map<String, List<String>>>() { });

        private final String keyName;

        private final ParameterizedTypeReference<?> typeRef;
    }

    <T> Mono<Map<String,T>> decodePart(final Map<String, Part> partsMap, final PartKey key);

    default Mono<GraphQlOperationsPart> decodeOperations(final Map<String, Part> partsMap) {
        return this.decodePart(partsMap, PartKey.OPERATIONS)
                .map(GraphQlOperationsPart::new);
    }

    default Mono<GraphQlFileMapPart> decodeFileMap(final Map<String, Part> partsMap) {
        return this.<List<String>>decodePart(partsMap, PartKey.MAP)
                .map(fm -> new GraphQlFileMapPart(fm, partsMap));
    }
}

