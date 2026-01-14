package com.yellowmoonsoftware.gmdb.controller;

import com.yellowmoonsoftware.gmdb.dto.output.PubSearchResult;
import com.yellowmoonsoftware.gmdb.dto.output.Transcriber;
import com.yellowmoonsoftware.gmdb.dto.output.Transcription;
import com.yellowmoonsoftware.gmdb.dto.output.TranscriptionPublication;
import com.yellowmoonsoftware.gmdb.mybatis.mappers.DataResolversMapper;
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
    private final DataResolversMapper mapper;

    @BatchMapping(field = "transcribers")
    public Mono<Map<Transcription, List<Transcriber>>> transcribers(final Set<Transcription> transcriptions) {
        final Map<Long, Transcription> transcriptionMap = transcriptions.stream()
                        .collect(toMap(Transcription::id, t -> t));

        return mapper.getTranscribersByTranscriptionIds(transcriptionMap.keySet())
                .collect(groupingBy(tt -> transcriptionMap.get(tt.transcriptionId()),
                        toList()));
    }

    @BatchMapping(field = "pub")
    public Mono<Map<Transcription, PubSearchResult>> pub(final Set<Transcription> transcriptions) {
        final Map<Long, Transcription> transcriptionMap = transcriptions.stream()
                .collect(toMap(Transcription::id, t -> t));

        return mapper.getPublicationByTranscriptionIds(transcriptionMap.keySet())
                .collectMap(TranscriptionPublication::transcriptionId)
                .map(m -> m.entrySet()
                        .stream()
                        .collect(toMap(e -> transcriptionMap.get(e.getKey()),
                                Map.Entry::getValue)));
    }
}
