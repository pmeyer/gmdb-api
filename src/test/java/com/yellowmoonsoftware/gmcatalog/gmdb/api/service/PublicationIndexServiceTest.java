package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.GMDBMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMutationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicationIndexServiceTest {

    @Mock
    private GMDBMapper gmdbMapper;

    @Mock
    private PubMutationMapper pubMutationMapper;

    @InjectMocks
    private PublicationIndexService publicationIndexService;

    @Test
    void upsertPublicationIndexDelegatesToUpsertForDataInput() {
        PubIndexInput input = new PubIndexInput(null, new PubIndexInput.Data("Guide", PubType.BOOK, "ISBN-1"));
        PubIndexOut output = new PubIndexOut(1L, "Guide", PubType.BOOK, "ISBN-1");
        when(pubMutationMapper.upsertPubIndex(input)).thenReturn(Mono.just(output));

        StepVerifier.create(publicationIndexService.upsertPublicationIndex(input))
            .expectNext(output)
            .verifyComplete();

        verify(pubMutationMapper).upsertPubIndex(input);
        verifyNoMoreInteractions(pubMutationMapper, gmdbMapper);
    }

    @Test
    void upsertPublicationIndexLoadsExistingIndexForReferenceInput() {
        PubIndexInput input = new PubIndexInput(1L, null);
        PubIndexOut output = new PubIndexOut(1L, "Guide", PubType.BOOK, "ISBN-1");
        when(gmdbMapper.getPubIndex(1L)).thenReturn(Mono.just(output));

        StepVerifier.create(publicationIndexService.upsertPublicationIndex(input))
            .expectNext(output)
            .verifyComplete();

        verify(gmdbMapper).getPubIndex(1L);
        verifyNoMoreInteractions(pubMutationMapper, gmdbMapper);
    }

    @Test
    void getPublicationIndicesDelegatesToMutationMapper() {
        PubIndexCriteria criteria = new PubIndexCriteria(PubType.BOOK);
        PubIndexOut output = new PubIndexOut(1L, "Guide", PubType.BOOK, "ISBN-1");
        when(pubMutationMapper.getPubIndices(criteria)).thenReturn(Flux.just(output));

        StepVerifier.create(publicationIndexService.getPublicationIndices(criteria))
            .expectNext(output)
            .verifyComplete();

        verify(pubMutationMapper).getPubIndices(criteria);
        verifyNoMoreInteractions(pubMutationMapper, gmdbMapper);
    }
}
