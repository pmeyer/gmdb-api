package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.BookDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.DataResolversMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.UUID;

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
        final TranscriptionInOut transcription = transcriptionInOut(10L, 20L, 1L);
        when(mapper.getSongTranscriptionsByPubIds(Set.of(1L))).thenReturn(Flux.just(transcription));

        StepVerifier.create(pubController.transcriptions(Set.of(publication)))
            .assertNext(result -> {
                assertThat(result).containsOnlyKeys(publication);
                assertThat(result.get(publication)).singleElement().satisfies(mapped -> {
                    assertThat(mapped.id()).isEqualTo(10L);
                    assertThat(mapped.url()).isEqualTo(transcription.details().transcriptionUrl());
                    assertThat(mapped.pageNumber()).isEqualTo(12);
                    assertThat(mapped.songId()).isEqualTo(20L);
                    assertThat(mapped.pubId()).isEqualTo(1L);
                });
            })
            .verifyComplete();

        verify(mapper).getSongTranscriptionsByPubIds(Set.of(1L));
    }

    private static TranscriptionInOut transcriptionInOut(final Long id, final Long songId, final Long pubId) {
        final TranscriptionDetails details = new TranscriptionDetails(12) {
            @Override
            public UUID resourceId() {
                return UUID.fromString("00000000-0000-0000-0000-000000000001");
            }
        };
        details.resources().put(ResourceSlug.TRANSCRIPTION, new ResourceAttributes("page.pdf", MediaType.APPLICATION_PDF));
        return new TranscriptionInOut(id, songId, pubId, details, null);
    }
}
