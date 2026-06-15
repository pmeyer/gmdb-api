package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class BookInputTest {

    @Test
    void exposesInheritedPublicationFieldsAndSupportedType() {
        final LocalDate pubDate = LocalDate.of(2024, 1, 15);
        final PubIndexInput index = new PubIndexInput(1L, null);
        final BookEditionInput info = new BookEditionInput("First", null);
        final List<TranscriptionInput> transcriptions = List.of(new TranscriptionInput(new SongInput(2L, null), 12, null, List.of()));

        final BookInput input = new BookInput(10L, pubDate, index, info, transcriptions);

        assertThat(input.id()).isEqualTo(10L);
        assertThat(input.pubDate()).isEqualTo(pubDate);
        assertThat(input.index()).isSameAs(index);
        assertThat(input.info()).isSameAs(info);
        assertThat(input.transcriptions()).isSameAs(transcriptions);
        assertThat(input.supportedPubType()).isEqualTo(PubType.BOOK);
    }

    @Test
    void cascadesValidationToIndex() {
        final BookInput input = new BookInput(
            null,
            LocalDate.of(2024, 1, 15),
            new PubIndexInput(null, null),
            new BookEditionInput("First", null),
            List.of()
        );

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactly(tuple("index", "PubIndexInput must have an ID or data"));
    }

    @Test
    void validatesRequiredFields() {
        final BookInput input = new BookInput(null, LocalDate.of(2024, 1, 15), null, null, List.of());

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactlyInAnyOrder(
                tuple("index", "must not be null"),
                tuple("info", "must not be null")
            );
    }

    @Test
    void cascadesValidationToInfo() {
        final BookInput input = new BookInput(
            null,
            LocalDate.of(2024, 1, 15),
            new PubIndexInput(1L, null),
            new BookEditionInput(null, null),
            List.of()
        );

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactly(tuple("info.edition", "must not be null"));
    }

    @Test
    void cascadesValidationToTranscriptions() {
        final BookInput input = new BookInput(
            null,
            LocalDate.of(2024, 1, 15),
            new PubIndexInput(1L, null),
            new BookEditionInput("First", null),
            List.of(new TranscriptionInput(new SongInput(null, null), 12, null, List.of()))
        );

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactly(tuple("transcriptions[0].song", "SongInput must have an ID or data"));
    }
}
