package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.MergeAction;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriberOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionTranscriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriberInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.TranscriberMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriberServiceTest {

    @Mock
    private TranscriberMapper transcriberMapper;

    @InjectMocks
    private TranscriberService transcriberService;

    @Test
    void upsertTranscriberDelegatesToUpsertForDataInput() {
        final TranscriberInput input = new TranscriberInput(null, "Alice");
        final TranscriberOut output = new TranscriberOut(1L, "Alice", MergeAction.INSERT);
        when(transcriberMapper.upsertTranscriber(input)).thenReturn(Mono.just(output));

        StepVerifier.create(transcriberService.upsertTranscriber(input))
            .expectNext(output)
            .verifyComplete();

        verify(transcriberMapper).upsertTranscriber(input);
        verifyNoMoreInteractions(transcriberMapper);
    }

    @Test
    void upsertTranscriberLoadsExistingForReferenceInput() {
        final TranscriberInput input = new TranscriberInput(1L, null);
        final TranscriberOut output = new TranscriberOut(1L, "Alice", null);
        when(transcriberMapper.getTranscriberById(1L)).thenReturn(Mono.just(output));

        StepVerifier.create(transcriberService.upsertTranscriber(input))
            .expectNext(output)
            .verifyComplete();

        verify(transcriberMapper).getTranscriberById(1L);
        verifyNoMoreInteractions(transcriberMapper);
    }

    @Test
    void upsertTranscriptionTranscribersDelegatesToMapper() {
        final List<TranscriptionTranscriber> input = List.of(TranscriptionTranscriber.forInput(1L, 2L));
        final TranscriptionTranscriber output = new TranscriptionTranscriber(1L, 2L, MergeAction.INSERT);
        when(transcriberMapper.upsertTranscriptionTranscribers(input)).thenReturn(Flux.just(output));

        StepVerifier.create(transcriberService.upsertTranscriptionTranscribers(input))
            .expectNext(output)
            .verifyComplete();

        verify(transcriberMapper).upsertTranscriptionTranscribers(input);
        verifyNoMoreInteractions(transcriberMapper);
    }

    @Test
    void clearTranscriptionTranscribersDelegatesToMapper() {
        when(transcriberMapper.clearTranscriptionTranscribers(1L)).thenReturn(Mono.just(2));

        StepVerifier.create(transcriberService.clearTranscriptionTranscribers(1L))
            .expectNext(2)
            .verifyComplete();

        verify(transcriberMapper).clearTranscriptionTranscribers(1L);
        verifyNoMoreInteractions(transcriberMapper);
    }
}
