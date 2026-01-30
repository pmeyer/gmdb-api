package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriptionInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.TranscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TranscriptionService {
    private final SongService songService;
    private final TranscriptionTranscriberService transcriptionTranscriberService;
    private final TranscriptionMapper transcriptionMapper;

    @Transactional
    public Mono<TranscriptionInOut> upsertTranscription(final Long pubId, final TranscriptionInput input) {
        return songService.upsertSong(input.song())
                .flatMap(s -> transcriptionMapper
                        .upsertTranscription(TranscriptionInOut.forNewTranscription(s.id(), pubId, input.toDetails())))
                .flatMap(tOut -> transcriptionTranscriberService
                        .addTranscriptionTranscribers(tOut.id(), input.transcribers())
                        .thenReturn(tOut));
    }
}

