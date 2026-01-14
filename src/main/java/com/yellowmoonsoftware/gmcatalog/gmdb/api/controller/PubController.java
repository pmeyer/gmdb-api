package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.config.FileResourceConfiguration;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.*;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.DataResolversMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
        final Map<Long, PubSearchResult> pubIdMap = pubs.stream().collect(toMap(PubSearchResult::id, p -> p));

        return mapper.getSongTranscriptionsByPubIds(pubIdMap.keySet())
                .collect(groupingBy(t -> pubIdMap.get(t.pubId())));
    }
}
