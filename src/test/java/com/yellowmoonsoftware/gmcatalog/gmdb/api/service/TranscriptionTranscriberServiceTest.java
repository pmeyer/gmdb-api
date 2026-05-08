package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriberOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionTranscriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriberInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriptionTranscriberServiceTest {

    @Mock
    private TranscriberService transcriberService;

    @InjectMocks
    private TranscriptionTranscriberService transcriptionTranscriberService;

    @Test
    @SuppressWarnings("unchecked")
    void addTranscriptionTranscribersBuildsReferenceAndUpsertedTranscribers() {
        TranscriberInput reference = new TranscriberInput(10L, null);
        TranscriberInput data = new TranscriberInput(null, "Alice");
        when(transcriberService.upsertTranscriber(data)).thenReturn(Mono.just(new TranscriberOut(20L, "Alice", null)));
        when(transcriberService.upsertTranscriptionTranscribers(org.mockito.ArgumentMatchers.anyList())).thenReturn(Flux.empty());

        StepVerifier.create(transcriptionTranscriberService.addTranscriptionTranscribers(1L, List.of(reference, data)))
            .verifyComplete();

        ArgumentCaptor<List<TranscriptionTranscriber>> captor = ArgumentCaptor.forClass(List.class);
        verify(transcriberService).upsertTranscriber(data);
        verify(transcriberService).upsertTranscriptionTranscribers(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue().get(0).transcriptionId()).isEqualTo(1L);
        assertThat(captor.getValue().get(0).transcriberId()).isEqualTo(10L);
        assertThat(captor.getValue().get(1).transcriptionId()).isEqualTo(1L);
        assertThat(captor.getValue().get(1).transcriberId()).isEqualTo(20L);
        verifyNoMoreInteractions(transcriberService);
    }

    @Test
    void addTranscriptionTranscribersTreatsNullListAsEmpty() {
        when(transcriberService.upsertTranscriptionTranscribers(List.of())).thenReturn(Flux.empty());

        StepVerifier.create(transcriptionTranscriberService.addTranscriptionTranscribers(1L, null))
            .verifyComplete();

        verify(transcriberService).upsertTranscriptionTranscribers(List.of());
        verifyNoMoreInteractions(transcriberService);
    }
}
