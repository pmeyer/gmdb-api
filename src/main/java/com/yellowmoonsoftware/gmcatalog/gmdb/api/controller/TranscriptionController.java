package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.Transcriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.Transcription;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.TranscriptionPublication;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.TranscriberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Controller
@RequiredArgsConstructor
@SchemaMapping(typeName = "Transcription")
public class TranscriptionController {
    private final TranscriberMapper transcriberMapper;
    private final PubMapper pubMapper;

    @BatchMapping(field = "transcribers")
    public Mono<Map<Transcription, List<Transcriber>>> transcribers(final Set<Transcription> transcriptions) {
        final Map<Long, Transcription> transcriptionMap = transcriptions.stream()
                        .collect(toMap(Transcription::id, t -> t));

        return transcriberMapper.getTranscribersByTranscriptionIds(transcriptionMap.keySet())
                .collect(groupingBy(tt -> transcriptionMap.get(tt.transcriptionId()),
                        toList()));
    }

    @BatchMapping(field = "pub")
    public Mono<Map<Transcription, PubSearchResult>> pub(final Set<Transcription> transcriptions) {
        final Map<Long, Transcription> transcriptionMap = transcriptions.stream()
                .collect(toMap(Transcription::id, t -> t));

        return pubMapper.getPublicationByTranscriptionIds(transcriptionMap.keySet())
                .collectMap(TranscriptionPublication::transcriptionId)
                .map(m -> m.entrySet()
                        .stream()
                        .collect(toMap(e -> transcriptionMap.get(e.getKey()),
                                Map.Entry::getValue)));
    }
}
