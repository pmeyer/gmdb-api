package com.yellowmoon.gmdb.graphql.multipartmapper;

import graphql.com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GraphQlMultipartJsonDecoder implements GraphQlMultipartDecoder {
    private final Decoder<?> jsonDecoder;

    @SuppressWarnings("unchecked")
    public <T> Mono<Map<String,T>> decodePart(final Map<String, Part> partsMap, final PartKey key) {
        return Optional.ofNullable(partsMap.get(key.getKeyName()))
                .map(part -> {
                    final Decoder<Map<String,T>> typedDecoder = (Decoder<Map<String,T>>) jsonDecoder;
                    return typedDecoder.decodeToMono(part.content(), ResolvableType.forType(key.getTypeRef()), MediaType.APPLICATION_JSON, null);
                })
                .orElse(Mono.just(Maps.newHashMap()));
    }
}
