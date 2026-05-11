package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriptionInputTest {

    @Mock
    private FilePart file;

    @Test
    void exposesTranscriptionFields() {
        final SongInput song = new SongInput(1L, null);
        final List<TranscriberInput> transcribers = List.of(new TranscriberInput(2L, null));

        final TranscriptionInput input = new TranscriptionInput(song, 12, file, transcribers);

        assertThat(input.song()).isSameAs(song);
        assertThat(input.pageNumber()).isEqualTo(12);
        assertThat(input.file()).isSameAs(file);
        assertThat(input.transcribers()).isSameAs(transcribers);
    }

    @Test
    void convertsToDetailsWithTranscriptionResource() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        when(file.filename()).thenReturn("page.pdf");
        when(file.headers()).thenReturn(headers);
        final TranscriptionInput input = new TranscriptionInput(new SongInput(1L, null), 12, file, List.of());

        final TranscriptionDetails details = input.toDetails();

        assertThat(details.pageNumber()).isEqualTo(12);
        assertThat(details.resources())
            .containsEntry(ResourceSlug.TRANSCRIPTION, new ResourceAttributes("page.pdf", MediaType.APPLICATION_PDF));
    }

    @Test
    void convertsToDetailsWithoutResourceWhenFileIsNull() {
        final TranscriptionInput input = new TranscriptionInput(new SongInput(1L, null), 12, null, List.of());

        final TranscriptionDetails details = input.toDetails();

        assertThat(details.pageNumber()).isEqualTo(12);
        assertThat(details.resources()).isEmpty();
    }
}
