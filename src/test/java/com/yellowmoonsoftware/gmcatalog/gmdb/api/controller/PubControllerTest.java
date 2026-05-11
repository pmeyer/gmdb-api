package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.BookDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.Transcription;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.DataResolversMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PubControllerTest {

    @Mock
    private DataResolversMapper mapper;

    @InjectMocks
    private PubController pubController;

    @Test
    void transcriptionsGroupsTranscriptionsByPublication() {
        final PubSearchResult publication = new PubSearchResult(1L, "Guide", PubType.BOOK, new BookDetails("First"), null, "ISBN-1", 2L);
        final Transcription transcription = new Transcription(10L, "url", 12, 20L, 1L);
        when(mapper.getSongTranscriptionsByPubIds(Set.of(1L))).thenReturn(Flux.just(transcription));

        StepVerifier.create(pubController.transcriptions(Set.of(publication)))
            .assertNext(result -> assertThat(result).containsEntry(publication, java.util.List.of(transcription)))
            .verifyComplete();

        verify(mapper).getSongTranscriptionsByPubIds(Set.of(1L));
    }
}
