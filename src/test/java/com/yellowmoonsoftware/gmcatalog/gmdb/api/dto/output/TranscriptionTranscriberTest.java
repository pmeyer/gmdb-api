package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TranscriptionTranscriberTest {

    @Test
    void exposesTranscriberAndTranscriptionFields() {
        final TranscriptionTranscriber transcriber = new TranscriptionTranscriber(1L, "Alice", 10L);

        assertThat(transcriber.id()).isEqualTo(1L);
        assertThat(transcriber.name()).isEqualTo("Alice");
        assertThat(transcriber.transcriptionId()).isEqualTo(10L);
    }

    @Test
    void equalityUsesInheritedIdAndTranscriptionId() {
        final TranscriptionTranscriber transcriber = new TranscriptionTranscriber(1L, "Alice", 10L);
        final TranscriptionTranscriber sameIdentity = new TranscriptionTranscriber(1L, "Different", 10L);
        final TranscriptionTranscriber differentTranscription = new TranscriptionTranscriber(1L, "Alice", 11L);

        assertThat(transcriber)
            .isEqualTo(sameIdentity)
            .hasSameHashCodeAs(sameIdentity)
            .isNotEqualTo(differentTranscription);
    }

    @Test
    void toStringIncludesInheritedAndLocalState() {
        final TranscriptionTranscriber transcriber = new TranscriptionTranscriber(1L, "Alice", 10L);

        assertThat(transcriber.toString())
            .contains("id=1")
            .contains("name=Alice")
            .contains("transcriptionId=10");
    }
}
