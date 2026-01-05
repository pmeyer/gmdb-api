package com.yellowmoonsoftware.gmdb.controller;

import com.yellowmoonsoftware.gmdb.config.FileResourceConfiguration;
import com.yellowmoonsoftware.gmdb.dto.output.*;
import com.yellowmoonsoftware.gmdb.mappers.DataResolversMapper;
import com.yellowmoonsoftware.gmdb.service.ResourceSlug;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.yellowmoonsoftware.gmdb.util.ReactiveUtils.async;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Controller
@RequiredArgsConstructor
@SchemaMapping(typeName = "PubSearchResult")
public class PubController {
    private final DataResolversMapper mapper;

    @SchemaMapping(field = "details")
    public Mono<PubDetails> pubDetails(final PubSearchResult pub) {
        if (pub.details().cover() == null) {
            return Mono.just(pub.details());
        }

        final String slugPath = ResourceSlug.COVER_IMAGE
                .getPath(Map.of("id", pub.id()), pub.details().cover());

        final String coverUrl = UriComponentsBuilder.fromPath(FileResourceConfiguration.RESOURCE_PATH)
                .pathSegment(slugPath)
                .build()
                .toString();

        return switch (pub.details()) {
            case MagDetails mag -> Mono.just(mag.toBuilder().cover(coverUrl).build());
            case BookDetails book -> Mono.just(book.toBuilder().cover(coverUrl).build());
            default -> Mono.just(pub.details());
        };
    }

    @BatchMapping(field = "transcriptions")
    public Mono<Map<PubSearchResult, List<Transcription>>> transcriptions(final Set<PubSearchResult> pubs) {
        return async(() -> {
            final Map<Long, PubSearchResult> pubIdMap = pubs.stream().collect(toMap(PubSearchResult::id, p -> p));

            return mapper.getSongTranscriptionsByPubIds(pubIdMap.keySet())
                    .stream()
                    .collect(groupingBy(t -> pubIdMap.get(t.pubId())));
        });
    }
}
