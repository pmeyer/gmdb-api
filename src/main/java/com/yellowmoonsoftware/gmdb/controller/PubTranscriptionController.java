package com.yellowmoonsoftware.gmdb.controller;

import com.yellowmoonsoftware.gmdb.dto.output.SongSearchResult;
import com.yellowmoonsoftware.gmdb.dto.output.Transcriber;
import com.yellowmoonsoftware.gmdb.dto.output.Transcription;
import com.yellowmoonsoftware.gmdb.mybatis.mappers.DataResolversMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Controller
@RequiredArgsConstructor
public class PubTranscriptionController {
    private final DataResolversMapper mapper;

    @BatchMapping(typeName = "PubTranscription", field = "song")
    public Mono<Map<Transcription, SongSearchResult>> song(final Set<Transcription> transcriptions) {
        final Map<Long, Transcription> transcriptionMap = transcriptions.stream()
                .collect(toMap(Transcription::songId, t -> t));

        return mapper.getSongsBySongIds(transcriptionMap.keySet())
                .collect(toMap(s -> transcriptionMap.get(s.id()), t -> t));
    }

    @BatchMapping(typeName = "PubTranscription", field = "transcribers")
    public Mono<Map<Transcription, List<Transcriber>>> transcribers(final Set<Transcription> transcriptions) {
        final Map<Long, Transcription> transcriptionMap = transcriptions.stream()
                .collect(toMap(Transcription::id, t -> t));

        return mapper.getTranscribersByTranscriptionIds(transcriptionMap.keySet())
                .collect(groupingBy(tt -> transcriptionMap.get(tt.transcriptionId()),
                        toList()));
    }
}
