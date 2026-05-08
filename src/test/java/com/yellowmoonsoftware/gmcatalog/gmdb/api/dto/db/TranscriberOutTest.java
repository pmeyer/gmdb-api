package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TranscriberOutTest {

    @Test
    void exposesTranscriberFieldsAndAction() {
        TranscriberOut transcriber = new TranscriberOut(1L, "Alice", MergeAction.INSERT);

        assertThat(transcriber.id()).isEqualTo(1L);
        assertThat(transcriber.name()).isEqualTo("Alice");
        assertThat(transcriber.action()).isEqualTo(MergeAction.INSERT);
    }
}
