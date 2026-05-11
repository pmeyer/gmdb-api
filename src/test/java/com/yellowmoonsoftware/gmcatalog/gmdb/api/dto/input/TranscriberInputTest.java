package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TranscriberInputTest {

    @Test
    void exposesRecordValuesAndDataContract() {
        final TranscriberInput input = new TranscriberInput(1L, "Alice");

        assertThat(input.id()).isEqualTo(1L);
        assertThat(input.name()).isEqualTo("Alice");
        assertThat(input.data()).isEqualTo("Alice");
        assertThat((IdAndDataContainer<String>) input).extracting(IdAndDataContainer::data)
            .isEqualTo("Alice");
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        final TranscriberInput input = new TranscriberInput(1L, "Alice");
        final TranscriberInput sameValues = new TranscriberInput(1L, "Alice");
        final TranscriberInput differentName = new TranscriberInput(1L, "Bob");

        assertThat(input)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentName);
        assertThat(input.toString()).contains("id=1", "name=Alice");
    }
}
