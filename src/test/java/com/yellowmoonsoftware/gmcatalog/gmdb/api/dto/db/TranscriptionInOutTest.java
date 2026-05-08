package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TranscriptionInOutTest {

    @Test
    void exposesTranscriptionFieldsAndAction() {
        TranscriptionDetails details = new TranscriptionDetails(12);

        TranscriptionInOut transcription = new TranscriptionInOut(1L, 2L, 3L, details, MergeAction.INSERT);

        assertThat(transcription.id()).isEqualTo(1L);
        assertThat(transcription.songId()).isEqualTo(2L);
        assertThat(transcription.pubId()).isEqualTo(3L);
        assertThat(transcription.details()).isSameAs(details);
        assertThat(transcription.action()).isEqualTo(MergeAction.INSERT);
    }

    @Test
    void forNewTranscriptionCreatesInsertInputWithoutIdOrAction() {
        TranscriptionDetails details = new TranscriptionDetails(12);

        TranscriptionInOut transcription = TranscriptionInOut.forNewTranscription(2L, 3L, details);

        assertThat(transcription.id()).isNull();
        assertThat(transcription.songId()).isEqualTo(2L);
        assertThat(transcription.pubId()).isEqualTo(3L);
        assertThat(transcription.details()).isSameAs(details);
        assertThat(transcription.action()).isNull();
    }
}
