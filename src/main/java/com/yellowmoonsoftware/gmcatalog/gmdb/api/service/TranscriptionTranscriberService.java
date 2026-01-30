package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionTranscriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriberInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TranscriptionTranscriberService {
    private final TranscriberService transcriberService;

    @Transactional
    public Mono<Void> addTranscriptionTranscribers(final Long transcriptionId, final List<TranscriberInput> transcribers) {
        return Flux.fromIterable(Optional.ofNullable(transcribers).orElse(List.of()))
                .flatMap(t -> {
                    if (t.mode() == IdAndDataContainer.DataMode.REF) {
                        return Mono.just(new TranscriptionTranscriber(transcriptionId, t.id(), null));
                    }
                    return transcriberService.upsertTranscriber(t)
                            .map(tOut -> new TranscriptionTranscriber(transcriptionId, tOut.id(), null));
                }, 4)
                .collectList()
                .flatMapMany(transcriberService::upsertTranscriptionTranscribers)
                .then();
    }
}
