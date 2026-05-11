package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class MagazineInputTest {

    @Test
    void exposesInheritedPublicationFieldsAndSupportedType() {
        final LocalDate pubDate = LocalDate.of(2024, 1, 15);
        final PubIndexInput index = new PubIndexInput(1L, null);
        final MagazineIssueInput info = new MagazineIssueInput("12", "4", "Winter", null);
        final List<TranscriptionInput> transcriptions = List.of(new TranscriptionInput(new SongInput(2L, null), 12, null, List.of()));

        final MagazineInput input = new MagazineInput(pubDate, index, info, transcriptions);

        assertThat(input.pubDate()).isEqualTo(pubDate);
        assertThat(input.index()).isSameAs(index);
        assertThat(input.info()).isSameAs(info);
        assertThat(input.transcriptions()).isSameAs(transcriptions);
        assertThat(input.supportedPubType()).isEqualTo(PubType.MAG);
    }

    @Test
    void cascadesValidationToIndex() {
        final MagazineInput input = new MagazineInput(
            LocalDate.of(2024, 1, 15),
            new PubIndexInput(null, null),
            new MagazineIssueInput("12", "4", "Winter", null),
            List.of()
        );

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactly(tuple("index", "PubIndexInput must have an ID or data"));
    }

    @Test
    void cascadesValidationToTranscriptions() {
        final MagazineInput input = new MagazineInput(
            LocalDate.of(2024, 1, 15),
            new PubIndexInput(1L, null),
            new MagazineIssueInput("12", "4", "Winter", null),
            List.of(new TranscriptionInput(new SongInput(null, null), 12, null, List.of()))
        );

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactly(tuple("transcriptions[0].song", "SongInput must have an ID or data"));
    }
}
