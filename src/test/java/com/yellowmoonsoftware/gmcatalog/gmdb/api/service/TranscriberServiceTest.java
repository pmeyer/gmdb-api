package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.MergeAction;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriberOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionTranscriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriberInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
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

import static org.assertj.core.api.Assertions.assertThat;
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
        when(transcriberMapper.getTranscriberId(1L)).thenReturn(Mono.just(1L));
        when(transcriberMapper.getTranscriberById(1L)).thenReturn(Mono.just(output));

        StepVerifier.create(transcriberService.upsertTranscriber(input))
            .expectNext(output)
            .verifyComplete();

        verify(transcriberMapper).getTranscriberId(1L);
        verify(transcriberMapper).getTranscriberById(1L);
        verifyNoMoreInteractions(transcriberMapper);
    }

    @Test
    void upsertTranscriberRejectsUnknownReferenceId() {
        final TranscriberInput input = new TranscriberInput(1L, null);
        when(transcriberMapper.getTranscriberId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(transcriberService.upsertTranscriber(input))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InvalidInputException.class);
                    assertThat(error).hasMessage("Unknown transcriber ID: 1");
                })
                .verify();

        verify(transcriberMapper).getTranscriberId(1L);
        verifyNoMoreInteractions(transcriberMapper);
    }

    @Test
    void upsertTranscriberValidatesExistingIdForIdAndDataInput() {
        final TranscriberInput input = new TranscriberInput(1L, "Alice");
        final TranscriberOut output = new TranscriberOut(1L, "Alice", MergeAction.UPDATE);
        when(transcriberMapper.getTranscriberId(1L)).thenReturn(Mono.just(1L));
        when(transcriberMapper.upsertTranscriber(input)).thenReturn(Mono.just(output));

        StepVerifier.create(transcriberService.upsertTranscriber(input))
                .expectNext(output)
                .verifyComplete();

        verify(transcriberMapper).getTranscriberId(1L);
        verify(transcriberMapper).upsertTranscriber(input);
        verifyNoMoreInteractions(transcriberMapper);
    }

    @Test
    void upsertTranscriberRejectsUnknownIdAndDataInputBeforeUpsert() {
        final TranscriberInput input = new TranscriberInput(1L, "Alice");
        when(transcriberMapper.getTranscriberId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(transcriberService.upsertTranscriber(input))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InvalidInputException.class);
                    assertThat(error).hasMessage("Unknown transcriber ID: 1");
                })
                .verify();

        verify(transcriberMapper).getTranscriberId(1L);
        verifyNoMoreInteractions(transcriberMapper);
    }

    @Test
    void validateTranscriberIdReturnsExistingId() {
        when(transcriberMapper.getTranscriberId(1L)).thenReturn(Mono.just(1L));

        StepVerifier.create(transcriberService.validateTranscriberId(1L))
                .expectNext(1L)
                .verifyComplete();

        verify(transcriberMapper).getTranscriberId(1L);
        verifyNoMoreInteractions(transcriberMapper);
    }

    @Test
    void validateTranscriberIdRejectsUnknownId() {
        when(transcriberMapper.getTranscriberId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(transcriberService.validateTranscriberId(1L))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InvalidInputException.class);
                    assertThat(error).hasMessage("Unknown transcriber ID: 1");
                })
                .verify();

        verify(transcriberMapper).getTranscriberId(1L);
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
