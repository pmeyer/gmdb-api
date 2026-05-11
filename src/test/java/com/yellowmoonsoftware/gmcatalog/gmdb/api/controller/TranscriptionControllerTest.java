package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.BookDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.Transcriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.Transcription;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.TranscriptionPublication;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.TranscriptionTranscriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.DataResolversMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriptionControllerTest {

    @Mock
    private DataResolversMapper mapper;

    @InjectMocks
    private TranscriptionController controller;

    @Test
    void transcribersGroupsByTranscription() {
        final Transcription transcription = new Transcription(1L, "url", 12, 2L, 3L);
        final TranscriptionTranscriber transcriber = new TranscriptionTranscriber(10L, "Alice", 1L);
        when(mapper.getTranscribersByTranscriptionIds(Set.of(1L))).thenReturn(Flux.just(transcriber));

        StepVerifier.create(controller.transcribers(Set.of(transcription)))
            .assertNext(result -> assertThat(result).containsEntry(transcription, List.of((Transcriber) transcriber)))
            .verifyComplete();

        verify(mapper).getTranscribersByTranscriptionIds(Set.of(1L));
    }

    @Test
    void pubMapsPublicationByTranscription() {
        final Transcription transcription = new Transcription(1L, "url", 12, 2L, 3L);
        final TranscriptionPublication publication = new TranscriptionPublication(3L, "Guide", PubType.BOOK, new BookDetails("First"), null, "ISBN-1", 4L, 1L);
        when(mapper.getPublicationByTranscriptionIds(Set.of(1L))).thenReturn(Flux.just(publication));

        StepVerifier.create(controller.pub(Set.of(transcription)))
            .assertNext(result -> assertThat(result).containsEntry(transcription, publication))
            .verifyComplete();

        verify(mapper).getPublicationByTranscriptionIds(Set.of(1L));
    }
}
