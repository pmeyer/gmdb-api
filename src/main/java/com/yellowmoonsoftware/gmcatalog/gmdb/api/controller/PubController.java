package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.*;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.TranscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
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
    private final TranscriptionMapper transcriptionMapper;

    @BatchMapping(field = "transcriptions")
    public Mono<Map<PubSearchResult, List<Transcription>>> transcriptions(final Set<PubSearchResult> pubs) {
        final Map<Long, PubSearchResult> pubIdMap = pubs.stream().collect(toMap(PubSearchResult::id, p -> p));

        return transcriptionMapper.getSongTranscriptionsByPubIds(pubIdMap.keySet())
                .map(Transcription::from)
                .collect(groupingBy(t -> pubIdMap.get(t.pubId())));
    }
}
