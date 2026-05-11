package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.SongSearchResult;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.Transcriber;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.Transcription;
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
class PubTranscriptionControllerTest {

    @Mock
    private DataResolversMapper mapper;

    @InjectMocks
    private PubTranscriptionController controller;

    @Test
    void songMapsSongsToAllMatchingTranscriptions() {
        final Transcription first = new Transcription(1L, "url1", 12, 10L, 100L);
        final Transcription second = new Transcription(2L, "url2", 13, 10L, 100L);
        final SongSearchResult song = new SongSearchResult(10L, "Opener", 3, 20L);
        when(mapper.getSongsBySongIds(Set.of(10L))).thenReturn(Flux.just(song));

        StepVerifier.create(controller.song(Set.of(first, second)))
            .assertNext(result -> assertThat(result)
                .containsEntry(first, song)
                .containsEntry(second, song))
            .verifyComplete();

        verify(mapper).getSongsBySongIds(Set.of(10L));
    }

    @Test
    void transcribersGroupsByTranscription() {
        final Transcription transcription = new Transcription(1L, "url", 12, 10L, 100L);
        final TranscriptionTranscriber transcriber = new TranscriptionTranscriber(20L, "Alice", 1L);
        when(mapper.getTranscribersByTranscriptionIds(Set.of(1L))).thenReturn(Flux.just(transcriber));

        StepVerifier.create(controller.transcribers(Set.of(transcription)))
            .assertNext(result -> assertThat(result).containsEntry(transcription, List.of((Transcriber) transcriber)))
            .verifyComplete();

        verify(mapper).getTranscribersByTranscriptionIds(Set.of(1L));
    }
}
