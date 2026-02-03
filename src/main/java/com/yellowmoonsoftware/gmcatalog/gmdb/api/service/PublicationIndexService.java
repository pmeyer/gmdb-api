package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.GMDBMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMutationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PublicationIndexService {
    private final GMDBMapper gmdbMapper;
    private final PubMutationMapper pubMutationMapper;

    public Mono<PubIndexOut> upsertPublicationIndex(final PubIndexInput pubIndexInput) {
        return pubIndexInput.mode() != IdAndDataContainer.DataMode.REF
                ? pubMutationMapper.upsertPubIndex(pubIndexInput)
                : gmdbMapper.getPubIndex(pubIndexInput.id());
    }

    public Flux<PubIndexOut> getPublicationIndices(final PubIndexCriteria criteria) {
        return pubMutationMapper.getPubIndices(criteria);
    }
}
