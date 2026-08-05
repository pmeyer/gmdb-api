package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriptionInputTest {

    @Mock
    private FilePart file;

    @Test
    void exposesIdAndData() {
        final TranscriptionData data = transcriptionData(file);

        final TranscriptionInput input = new TranscriptionInput(1L, data);

        assertThat(input.id()).isEqualTo(1L);
        assertThat(input.data()).isSameAs(data);
        assertThat(input.mode()).isEqualTo(IdAndDataContainer.DataMode.UPDATE);
    }

    @Test
    void acceptsIdWithoutDataAsReference() {
        final TranscriptionInput input = new TranscriptionInput(1L, null);

        assertThat(ValidationTestSupport.validate(input)).isEmpty();
        assertThat(input.mode()).isEqualTo(IdAndDataContainer.DataMode.REF);
    }

    @Test
    void acceptsDataWithoutIdAsAddition() {
        final TranscriptionInput input = new TranscriptionInput(null, transcriptionData(null));

        assertThat(ValidationTestSupport.validate(input)).isEmpty();
        assertThat(input.mode()).isEqualTo(IdAndDataContainer.DataMode.ADD);
    }

    @Test
    void requiresIdWhenDataIsNull() {
        final TranscriptionInput input = new TranscriptionInput(null, null);

        assertThat(ValidationTestSupport.validate(input))
                .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
                .containsExactly(tuple("", "TranscriptionInput must have an ID or data"));
    }

    @Test
    void exposesTranscriptionDataFields() {
        final SongInput song = new SongInput(1L, null);
        final List<TranscriberInput> transcribers = List.of(new TranscriberInput(2L, null));

        final TranscriptionData data = new TranscriptionData(song, 12, file, transcribers);

        assertThat(data.song()).isSameAs(song);
        assertThat(data.pageNumber()).isEqualTo(12);
        assertThat(data.file()).isSameAs(file);
        assertThat(data.transcribers()).isSameAs(transcribers);
    }

    @Test
    void convertsDataToDetailsWithTranscriptionResource() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        when(file.filename()).thenReturn("page.pdf");
        when(file.headers()).thenReturn(headers);
        final TranscriptionData data = transcriptionData(file);

        final TranscriptionDetails details = data.toDetails();

        assertThat(details.pageNumber()).isEqualTo(12);
        assertThat(details.resources())
                .containsEntry(ResourceSlug.TRANSCRIPTION,
                        new ResourceAttributes("page.pdf", MediaType.APPLICATION_PDF));
    }

    @Test
    void convertsDataToDetailsWithoutResourceWhenFileIsNull() {
        final TranscriptionDetails details = transcriptionData(null).toDetails();

        assertThat(details.pageNumber()).isEqualTo(12);
        assertThat(details.resources()).isEmpty();
    }

    @Test
    void validatesRequiredDataFields() {
        final TranscriptionInput input = new TranscriptionInput(null,
                new TranscriptionData(null, null, file, List.of()));

        assertThat(ValidationTestSupport.validate(input))
                .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        tuple("data.song", "must not be null"),
                        tuple("data.pageNumber", "must not be null")
                );
    }

    @Test
    void cascadesValidationToSong() {
        final TranscriptionInput input = new TranscriptionInput(null,
                new TranscriptionData(new SongInput(null, null), 12, file, List.of()));

        assertThat(ValidationTestSupport.validate(input))
                .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
                .containsExactly(tuple("data.song", "SongInput must have an ID or data"));
    }

    @Test
    void cascadesValidationToTranscribers() {
        final TranscriptionInput input = new TranscriptionInput(null,
                new TranscriptionData(new SongInput(1L, null), 12, file,
                        List.of(new TranscriberInput(null, null))));

        assertThat(ValidationTestSupport.validate(input))
                .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
                .containsExactly(tuple("data.transcribers[0]",
                        "TranscriberInput must have an ID or data (or both)"));
    }

    private static TranscriptionData transcriptionData(final FilePart file) {
        return new TranscriptionData(new SongInput(1L, null), 12, file, List.of());
    }
}
