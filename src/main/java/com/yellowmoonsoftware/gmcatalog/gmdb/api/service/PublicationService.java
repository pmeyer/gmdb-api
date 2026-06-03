package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AbstractPubInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InputValidationException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class PublicationService {
    private final PubMapper pubMapper;

    private final PublicationIndexService pubIndexService;
    private final FileService fileService;
    private final TranscriptionService transcriptionService;

    @Transactional
    public Mono<PubSearchResult> addPub(final AbstractPubInput<?, ? extends PubDetails> pubInput) {
        return validatePublication(pubInput)
                .then(Mono.defer(() -> pubIndexService.upsertPublicationIndex(pubInput.index())))
                .handle(Validation.expectedPubIndexPubType(pubInput.supportedPubType()))
                .flatMap(pubIdx -> pubMapper.upsertPublication(PubIn.from(pubIdx.id(), pubInput)))
                .flatMap(out -> {
                    final Mono<ResourceReference> fileSignal = Mono.justOrEmpty(pubInput.info().cover())
                            .flatMap(blob -> fileService.put(blob, ResourceSlug.COVER_IMAGE, Map.of("id", out.details().resourceId())));

                    final Flux<TranscriptionInOut> transcriptionSignal = Flux.fromIterable(Optional.ofNullable(pubInput.transcriptions()).orElse(List.of()))
                            .flatMap(t -> transcriptionService.upsertTranscription(out.id(), t));

                    return Mono.when(fileSignal, transcriptionSignal)
                            .then(pubMapper.getPub(out.id()));
                });
    }

    private Mono<PubSearchResult> validatePublication(final AbstractPubInput<?, ? extends PubDetails> pubInput) {
        return pubInput.id() == null
                ? Mono.empty()
                : pubMapper.getPub(pubInput.id())
                    .switchIfEmpty(Mono.error(new InvalidInputException("Unknown publication ID: " + pubInput.id())))
                    .handle(Validation.expectedPublicationPubType(pubInput.supportedPubType()));
    }

    static class Validation {
        public static BiConsumer<PubIndexOut, SynchronousSink<PubIndexOut>> expectedPubIndexPubType(final PubType expected) {
            return (pubIdxOut, sink) -> {
                if (expected != pubIdxOut.type()) {
                    sink.error(new InputValidationException(
                            String.format("Pub type mismatch: input is for pub type %s, but pub index specified is for pub type %s.",
                                    expected, pubIdxOut.type())));
                } else {
                    sink.next(pubIdxOut);
                }
            };
        }

        public static BiConsumer<PubSearchResult, SynchronousSink<PubSearchResult>> expectedPublicationPubType(final PubType expected) {
            return (pubOut, sink) -> {
                if (expected != pubOut.type()) {
                    sink.error(new InputValidationException(
                            String.format("Pub type mismatch: input is for pub type %s, but publication specified is for pub type %s.",
                                    expected, pubOut.type())));
                } else {
                    sink.next(pubOut);
                }
            };
        }
    }
}
