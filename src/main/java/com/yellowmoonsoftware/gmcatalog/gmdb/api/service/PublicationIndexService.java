package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubIndexMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PublicationIndexService {
    private final PubIndexMapper pubIndexMapper;

    public Mono<PubIndexOut> upsertPublicationIndex(final PubIndexInput pubIndexInput) {
        final Mono<PubIndexOut> upsertSignal = pubIndexInput.mode() != IdAndDataContainer.DataMode.REF
                ? Mono.defer(() -> pubIndexMapper.upsertPubIndex(pubIndexInput))
                : Mono.defer(() -> pubIndexMapper.getPubIndex(pubIndexInput.id()));

        return pubIndexInput.id() == null
                ? upsertSignal
                : pubIndexMapper.getPubIndexId(pubIndexInput.id())
                    .switchIfEmpty(Mono.error(new InvalidInputException("Unknown publication index ID: " + pubIndexInput.id())))
                    .then(upsertSignal);
    }

    public Flux<PubIndexOut> getPublicationIndices(final PubIndexCriteria criteria) {
        return pubIndexMapper.getPubIndices(criteria);
    }
}
