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

@Service
@RequiredArgsConstructor
public class TranscriptionTranscriberService {
    private final TranscriberService transcriberService;

    @Transactional
    public Mono<Void> addTranscriptionTranscribers(final Long transcriptionId, final List<TranscriberInput> transcribers) {
        if (transcribers == null) {
            return Mono.empty();
        }
        if (transcribers.isEmpty()) {
            return transcriberService.clearTranscriptionTranscribers(transcriptionId).then();
        }
        return Flux.fromIterable(transcribers)
                .flatMap(t -> {
                    if (t.mode() == IdAndDataContainer.DataMode.REF) {
                        return transcriberService.validateTranscriberId(t.id())
                                .map(transcriberId -> new TranscriptionTranscriber(transcriptionId, transcriberId, null));
                    }
                    return transcriberService.upsertTranscriber(t)
                            .map(tOut -> new TranscriptionTranscriber(transcriptionId, tOut.id(), null));
                }, 4)
                .collectList()
                .flatMapMany(transcriberService::upsertTranscriptionTranscribers)
                .then();
    }
}
