package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionDetails;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.TranscriptionInOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TranscriptionTest {

    @Test
    void exposesRecordValues() {
        final Transcription transcription = new Transcription(1L, "/resources/transcriptions/one.pdf", 12, 20L, 30L);

        assertThat(transcription.id()).isEqualTo(1L);
        assertThat(transcription.url()).isEqualTo("/resources/transcriptions/one.pdf");
        assertThat(transcription.pageNumber()).isEqualTo(12);
        assertThat(transcription.songId()).isEqualTo(20L);
        assertThat(transcription.pubId()).isEqualTo(30L);
    }

    @Test
    void fromMapsTranscriptionInOutToGraphQlTranscription() {
        final TranscriptionDetails details = new TranscriptionDetails(12) {
            @Override
            public UUID resourceId() {
                return UUID.fromString("00000000-0000-0000-0000-000000000001");
            }
        };
        details.resources().put(ResourceSlug.TRANSCRIPTION, new ResourceAttributes("page.pdf", MediaType.APPLICATION_PDF));
        final TranscriptionInOut input = new TranscriptionInOut(1L, 20L, 30L, details, null);

        final Transcription transcription = Transcription.from(input);

        assertThat(transcription.id()).isEqualTo(1L);
        assertThat(transcription.url()).isEqualTo(details.transcriptionUrl());
        assertThat(transcription.pageNumber()).isEqualTo(12);
        assertThat(transcription.songId()).isEqualTo(20L);
        assertThat(transcription.pubId()).isEqualTo(30L);
    }

    @Test
    void equalityUsesOnlyId() {
        final Transcription transcription = new Transcription(1L, "/one.pdf", 12, 20L, 30L);
        final Transcription sameId = new Transcription(1L, "/other.pdf", 99, 21L, 31L);
        final Transcription differentId = new Transcription(2L, "/one.pdf", 12, 20L, 30L);

        assertThat(transcription)
            .isEqualTo(sameId)
            .hasSameHashCodeAs(sameId)
            .isNotEqualTo(differentId);
    }

    @Test
    void equalityRejectsDifferentTypes() {
        final Transcription transcription = new Transcription(1L, "/one.pdf", 12, 20L, 30L);

        assertThat(transcription).isNotEqualTo("1");
    }

    @Test
    void equalityAndHashCodeHandleNullIds() {
        final Transcription transcription = new Transcription(null, "/one.pdf", 12, 20L, 30L);
        final Transcription sameNullId = new Transcription(null, "/other.pdf", 99, 21L, 31L);

        assertThat(transcription)
            .isEqualTo(sameNullId)
            .hasSameHashCodeAs(sameNullId);
        assertThat(transcription.hashCode()).isZero();
    }

    @Test
    void toStringIncludesRecordValues() {
        final Transcription transcription = new Transcription(1L, "/one.pdf", 12, 20L, 30L);

        assertThat(transcription.toString()).contains("id=1", "url=/one.pdf", "pageNumber=12");
    }
}
