package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TranscriptionTranscriberTest {

    @Test
    void exposesTranscriptionTranscriberFieldsAndAction() {
        TranscriptionTranscriber transcriber = new TranscriptionTranscriber(1L, 2L, MergeAction.INSERT);

        assertThat(transcriber.transcriptionId()).isEqualTo(1L);
        assertThat(transcriber.transcriberId()).isEqualTo(2L);
        assertThat(transcriber.action()).isEqualTo(MergeAction.INSERT);
    }

    @Test
    void forInputCreatesValueWithoutAction() {
        TranscriptionTranscriber transcriber = TranscriptionTranscriber.forInput(1L, 2L);

        assertThat(transcriber.transcriptionId()).isEqualTo(1L);
        assertThat(transcriber.transcriberId()).isEqualTo(2L);
        assertThat(transcriber.action()).isNull();
    }
}
