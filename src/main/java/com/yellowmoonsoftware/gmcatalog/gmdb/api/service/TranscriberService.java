package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriberOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionTranscriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriberInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
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
        final Mono<TranscriberOut> upsertSignal = transcriber.mode() == IdAndDataContainer.DataMode.REF
                ? Mono.defer(() -> transcriberMapper.getTranscriberById(transcriber.id()))
                : Mono.defer(() -> transcriberMapper.upsertTranscriber(transcriber));

        return transcriber.id() == null
                ? upsertSignal
                : validateTranscriberId(transcriber.id()).then(upsertSignal);
    }

    public Mono<Long> validateTranscriberId(final Long id) {
        return transcriberMapper.getTranscriberId(id)
                .switchIfEmpty(Mono.error(new InvalidInputException("Unknown transcriber ID: " + id)));
    }

    public Flux<TranscriptionTranscriber> upsertTranscriptionTranscribers(final List<TranscriptionTranscriber> transcriptionTranscribers) {
        return transcriberMapper.upsertTranscriptionTranscribers(transcriptionTranscribers);
    }

    public Mono<Integer> clearTranscriptionTranscribers(final Long transcriptionId) {
        return transcriberMapper.clearTranscriptionTranscribers(transcriptionId);
    }
}
