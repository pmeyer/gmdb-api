package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriberOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionTranscriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriberInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.TranscriberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TranscriberService {
    private final TranscriberMapper transcriberMapper;

    public Mono<TranscriberOut> upsertTranscriber(final TranscriberInput transcriber) {
        if (transcriber.mode() == IdAndDataContainer.DataMode.REF) {
            return transcriberMapper.getTranscriberById(transcriber.id());
        }
        return transcriberMapper.upsertTranscriber(transcriber);
    }

    public Flux<TranscriptionTranscriber> upsertTranscriptionTranscribers(final List<TranscriptionTranscriber> transcriptionTranscribers) {
        return transcriberMapper.upsertTranscriptionTranscribers(transcriptionTranscribers);
    }
}
