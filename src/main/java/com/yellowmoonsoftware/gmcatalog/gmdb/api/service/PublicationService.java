package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AbstractPubInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InputValidationException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.GMDBMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMutationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.util.Map;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class PublicationService {
    private final GMDBMapper gmdbMapper;
    private final PubMutationMapper pubMutationMapper;

    private final PublicationIndexService pubIndexService;
    private final FileService fileService;

    @Transactional
    public Mono<PubSearchResult> addPub(final AbstractPubInput<?, ? extends PubDetails> pubInput) {
        return pubIndexService.upsertPublicationIndex(pubInput.index())
                .handle(Validation.expectedPubIndexPubType(pubInput.supportedPubType()))
                .flatMap(pubIdx -> pubMutationMapper.upsertPublication(PubIn.forNewPub(pubIdx.id(), pubInput)))
                .flatMap(out -> Mono.justOrEmpty(pubInput.info().cover())
                        .flatMap(blob -> fileService.put(blob, ResourceSlug.COVER_IMAGE, Map.of("id", out.details().resourceId())))
                        .thenReturn(out.id()))
                .flatMap(gmdbMapper::getPub);
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
    }
}

