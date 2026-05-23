package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.PubIndexOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexCriteria;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.GMDBMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubIndexMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMutationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicationIndexServiceTest {

    @Mock
    private GMDBMapper gmdbMapper;

    @Mock
    private PubIndexMapper pubIndexMapper;

    @Mock
    private PubMutationMapper pubMutationMapper;

    @InjectMocks
    private PublicationIndexService publicationIndexService;

    @Test
    void upsertPublicationIndexDelegatesToUpsertForDataInput() {
        final PubIndexInput input = new PubIndexInput(null, new PubIndexInput.Data("Guide", PubType.BOOK, "ISBN-1"));
        final PubIndexOut output = new PubIndexOut(1L, "Guide", PubType.BOOK, "ISBN-1");
        when(pubMutationMapper.upsertPubIndex(input)).thenReturn(Mono.just(output));

        StepVerifier.create(publicationIndexService.upsertPublicationIndex(input))
            .expectNext(output)
            .verifyComplete();

        verify(pubMutationMapper).upsertPubIndex(input);
        verifyNoMoreInteractions(pubMutationMapper, gmdbMapper, pubIndexMapper);
    }

    @Test
    void upsertPublicationIndexLoadsExistingIndexForReferenceInput() {
        final PubIndexInput input = new PubIndexInput(1L, null);
        final PubIndexOut output = new PubIndexOut(1L, "Guide", PubType.BOOK, "ISBN-1");
        when(pubIndexMapper.getPubIndexId(1L)).thenReturn(Mono.just(1L));
        when(gmdbMapper.getPubIndex(1L)).thenReturn(Mono.just(output));

        StepVerifier.create(publicationIndexService.upsertPublicationIndex(input))
            .expectNext(output)
            .verifyComplete();

        verify(pubIndexMapper).getPubIndexId(1L);
        verify(gmdbMapper).getPubIndex(1L);
        verifyNoMoreInteractions(pubMutationMapper, gmdbMapper, pubIndexMapper);
    }

    @Test
    void upsertPublicationIndexValidatesExistingIndexForIdAndDataInput() {
        final PubIndexInput input = new PubIndexInput(1L, new PubIndexInput.Data("Guide", PubType.BOOK, "ISBN-1"));
        final PubIndexOut output = new PubIndexOut(1L, "Guide", PubType.BOOK, "ISBN-1");
        when(pubIndexMapper.getPubIndexId(1L)).thenReturn(Mono.just(1L));
        when(pubMutationMapper.upsertPubIndex(input)).thenReturn(Mono.just(output));

        StepVerifier.create(publicationIndexService.upsertPublicationIndex(input))
                .expectNext(output)
                .verifyComplete();

        verify(pubIndexMapper).getPubIndexId(1L);
        verify(pubMutationMapper).upsertPubIndex(input);
        verifyNoMoreInteractions(pubMutationMapper, gmdbMapper, pubIndexMapper);
    }

    @Test
    void upsertPublicationIndexRejectsUnknownReferenceId() {
        final PubIndexInput input = new PubIndexInput(1L, null);
        when(pubIndexMapper.getPubIndexId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(publicationIndexService.upsertPublicationIndex(input))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InvalidInputException.class);
                    assertThat(error).hasMessage("Unknown publication index ID: 1");
                })
                .verify();

        verify(pubIndexMapper).getPubIndexId(1L);
        verifyNoMoreInteractions(pubIndexMapper);
        verifyNoMoreInteractions(pubMutationMapper, gmdbMapper);
    }

    @Test
    void upsertPublicationIndexRejectsUnknownIdAndDataInputBeforeUpsert() {
        final PubIndexInput input = new PubIndexInput(1L, new PubIndexInput.Data("Guide", PubType.BOOK, "ISBN-1"));
        when(pubIndexMapper.getPubIndexId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(publicationIndexService.upsertPublicationIndex(input))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InvalidInputException.class);
                    assertThat(error).hasMessage("Unknown publication index ID: 1");
                })
                .verify();

        verify(pubIndexMapper).getPubIndexId(1L);
        verifyNoMoreInteractions(pubIndexMapper);
        verifyNoMoreInteractions(pubMutationMapper, gmdbMapper);
    }

    @Test
    void getPublicationIndicesDelegatesToMutationMapper() {
        final PubIndexCriteria criteria = new PubIndexCriteria(PubType.BOOK);
        final PubIndexOut output = new PubIndexOut(1L, "Guide", PubType.BOOK, "ISBN-1");
        when(pubMutationMapper.getPubIndices(criteria)).thenReturn(Flux.just(output));

        StepVerifier.create(publicationIndexService.getPublicationIndices(criteria))
            .expectNext(output)
            .verifyComplete();

        verify(pubMutationMapper).getPubIndices(criteria);
        verifyNoMoreInteractions(pubMutationMapper, gmdbMapper, pubIndexMapper);
    }
}
