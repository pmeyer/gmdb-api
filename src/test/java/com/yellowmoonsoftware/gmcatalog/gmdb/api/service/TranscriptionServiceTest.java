package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.SongOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.SongInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriberInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.TranscriptionInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.PubMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.TranscriptionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriptionServiceTest {

    @Mock
    private SongService songService;

    @Mock
    private TranscriptionTranscriberService transcriptionTranscriberService;

    @Mock
    private FileService fileService;

    @Mock
    private PubMapper pubMapper;

    @Mock
    private TranscriptionMapper transcriptionMapper;

    @Mock
    private FilePart file;

    @InjectMocks
    private TranscriptionService transcriptionService;

    @Test
    void upsertTranscriptionStoresFileAndTranscribers() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        when(file.filename()).thenReturn("page.pdf");
        when(file.headers()).thenReturn(headers);
        final SongInput songInput = new SongInput(1L, null);
        final List<TranscriberInput> transcribers = List.of(new TranscriberInput(2L, null));
        final TranscriptionInput input = new TranscriptionInput(songInput, 12, file, transcribers);
        final TranscriptionInOut output = transcriptionOut();
        when(pubMapper.getPubId(20L)).thenReturn(Mono.just(20L));
        when(songService.upsertSong(songInput)).thenReturn(Mono.just(new SongOut(10L, "Opener", null, null, null)));
        when(transcriptionMapper.upsertTranscription(any(TranscriptionInOut.class))).thenReturn(Mono.just(output));
        when(fileService.put(file, ResourceSlug.TRANSCRIPTION, Map.of("id", output.details().resourceId())))
            .thenReturn(Mono.just(new ResourceReference(ResourceSlug.TRANSCRIPTION, "transcription/1", "page.pdf")));
        when(transcriptionTranscriberService.addTranscriptionTranscribers(1L, transcribers)).thenReturn(Mono.empty());

        StepVerifier.create(transcriptionService.upsertTranscription(20L, input))
            .expectNext(output)
            .verifyComplete();

        final ArgumentCaptor<TranscriptionInOut> captor = ArgumentCaptor.forClass(TranscriptionInOut.class);
        verify(pubMapper).getPubId(20L);
        verify(songService).upsertSong(songInput);
        verify(transcriptionMapper).upsertTranscription(captor.capture());
        assertThat(captor.getValue().songId()).isEqualTo(10L);
        assertThat(captor.getValue().pubId()).isEqualTo(20L);
        assertThat(captor.getValue().details().pageNumber()).isEqualTo(12);
        verify(fileService).put(file, ResourceSlug.TRANSCRIPTION, Map.of("id", output.details().resourceId()));
        verify(transcriptionTranscriberService).addTranscriptionTranscribers(1L, transcribers);
        verifyNoMoreInteractions(pubMapper, songService, transcriptionMapper, fileService, transcriptionTranscriberService);
    }

    @Test
    void upsertTranscriptionAllowsMissingFile() {
        final SongInput songInput = new SongInput(1L, null);
        final TranscriptionInput input = new TranscriptionInput(songInput, 12, null, List.of());
        final TranscriptionInOut output = transcriptionOut();
        when(pubMapper.getPubId(20L)).thenReturn(Mono.just(20L));
        when(songService.upsertSong(songInput)).thenReturn(Mono.just(new SongOut(10L, "Opener", null, null, null)));
        when(transcriptionMapper.upsertTranscription(any(TranscriptionInOut.class))).thenReturn(Mono.just(output));
        when(transcriptionTranscriberService.addTranscriptionTranscribers(1L, List.of())).thenReturn(Mono.empty());

        StepVerifier.create(transcriptionService.upsertTranscription(20L, input))
            .expectNext(output)
            .verifyComplete();

        verify(pubMapper).getPubId(20L);
        verify(songService).upsertSong(songInput);
        verify(transcriptionMapper).upsertTranscription(any(TranscriptionInOut.class));
        verify(transcriptionTranscriberService).addTranscriptionTranscribers(1L, List.of());
        verifyNoInteractions(fileService);
        verifyNoMoreInteractions(pubMapper, songService, transcriptionMapper, transcriptionTranscriberService);
    }

    @Test
    void upsertTranscriptionRejectsUnknownPublicationIdBeforeUpsertingSong() {
        final SongInput songInput = new SongInput(1L, null);
        final TranscriptionInput input = new TranscriptionInput(songInput, 12, null, List.of());
        when(pubMapper.getPubId(20L)).thenReturn(Mono.empty());

        StepVerifier.create(transcriptionService.upsertTranscription(20L, input))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InvalidInputException.class);
                    assertThat(error).hasMessage("Unknown publication ID: 20");
                })
                .verify();

        verify(pubMapper).getPubId(20L);
        verifyNoMoreInteractions(pubMapper);
        verifyNoInteractions(songService, transcriptionMapper, fileService, transcriptionTranscriberService);
    }

    private static TranscriptionInOut transcriptionOut() {
        final TranscriptionDetails details = new TranscriptionDetails(12) {
            @Override
            public UUID resourceId() {
                return UUID.fromString("00000000-0000-0000-0000-000000000001");
            }
        };
        return new TranscriptionInOut(1L, 10L, 20L, details, null);
    }
}
