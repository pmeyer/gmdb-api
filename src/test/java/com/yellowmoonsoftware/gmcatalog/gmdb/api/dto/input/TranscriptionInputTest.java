package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
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

    @Test
    void validatesRequiredFields() {
        final TranscriptionInput input = new TranscriptionInput(null, null, file, List.of());

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactlyInAnyOrder(
                tuple("song", "must not be null"),
                tuple("pageNumber", "must not be null")
            );
    }

    @Test
    void cascadesValidationToSong() {
        final TranscriptionInput input = new TranscriptionInput(new SongInput(null, null), 12, file, List.of());

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactly(tuple("song", "SongInput must have an ID or data"));
    }

    @Test
    void cascadesValidationToTranscribers() {
        final TranscriptionInput input = new TranscriptionInput(new SongInput(1L, null), 12, file, List.of(new TranscriberInput(null, null)));

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactly(tuple("transcribers[0]", "TranscriberInput must have an ID or data (or both)"));
    }

    @Test
    void validatesRequiredConstructorFileParameter() throws NoSuchMethodException {
        final Constructor<TranscriptionInput> constructor = TranscriptionInput.class.getConstructor(
            SongInput.class,
            Integer.class,
            FilePart.class,
            List.class
        );

        assertThat(ValidationTestSupport.validateConstructorParameters(
            constructor,
            new SongInput(1L, null),
            12,
            null,
            List.of()
        ))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactly(tuple("TranscriptionInput.file", "must not be null"));
    }
}
