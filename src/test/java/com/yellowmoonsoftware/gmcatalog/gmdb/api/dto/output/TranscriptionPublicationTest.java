package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TranscriptionPublicationTest {

    @Test
    void exposesPublicationAndTranscriptionFields() {
        final BookDetails details = new BookDetails("First");
        final LocalDate pubDate = LocalDate.of(2024, 3, 15);

        final TranscriptionPublication publication = new TranscriptionPublication(
            1L,
            "Guide",
            PubType.BOOK,
            details,
            pubDate,
            "ISBN-1",
            100L,
            200L
        );

        assertThat(publication.id()).isEqualTo(1L);
        assertThat(publication.name()).isEqualTo("Guide");
        assertThat(publication.type()).isEqualTo(PubType.BOOK);
        assertThat(publication.details()).isSameAs(details);
        assertThat(publication.pubDate()).isEqualTo(pubDate);
        assertThat(publication.serialNumber()).isEqualTo("ISBN-1");
        assertThat(publication.pubIndexId()).isEqualTo(100L);
        assertThat(publication.transcriptionId()).isEqualTo(200L);
    }

    @Test
    void equalityUsesPublicationIdAndTranscriptionId() {
        final TranscriptionPublication publication = publication(1L, 200L);
        final TranscriptionPublication sameIdentity = publication(1L, 200L);
        final TranscriptionPublication differentPublication = publication(2L, 200L);
        final TranscriptionPublication differentTranscription = publication(1L, 201L);

        assertThat(publication)
            .isEqualTo(sameIdentity)
            .hasSameHashCodeAs(sameIdentity)
            .isNotEqualTo(differentPublication)
            .isNotEqualTo(differentTranscription);
    }

    @Test
    void toStringIncludesInheritedAndLocalState() {
        final TranscriptionPublication publication = publication(1L, 200L);

        assertThat(publication.toString())
            .contains("id=1")
            .contains("name=Guide")
            .contains("type=BOOK")
            .contains("transcriptionId=200");
    }

    private static TranscriptionPublication publication(Long id, Long transcriptionId) {
        return new TranscriptionPublication(
            id,
            "Guide",
            PubType.BOOK,
            new BookDetails("First"),
            LocalDate.of(2024, 3, 15),
            "ISBN-1",
            100L,
            transcriptionId
        );
    }
}
