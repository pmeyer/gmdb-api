package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PubIndexInputTest {

    @Test
    void exposesRecordValuesAndDataContract() {
        final PubIndexInput.Data data = new PubIndexInput.Data("Guide", PubType.BOOK, "ISBN-1");

        final PubIndexInput input = new PubIndexInput(1L, data);

        assertThat(input.id()).isEqualTo(1L);
        assertThat(input.data()).isSameAs(data);
        assertThat((IdAndDataContainer<PubIndexInput.Data>) input).extracting(IdAndDataContainer::data)
            .isSameAs(data);
    }

    @Test
    void dataRecordExposesValues() {
        final PubIndexInput.Data data = new PubIndexInput.Data("Guide", PubType.BOOK, "ISBN-1");

        assertThat(data.name()).isEqualTo("Guide");
        assertThat(data.type()).isEqualTo(PubType.BOOK);
        assertThat(data.serial()).isEqualTo("ISBN-1");
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        final PubIndexInput input = new PubIndexInput(1L, new PubIndexInput.Data("Guide", PubType.BOOK, "ISBN-1"));
        final PubIndexInput sameValues = new PubIndexInput(1L, new PubIndexInput.Data("Guide", PubType.BOOK, "ISBN-1"));
        final PubIndexInput differentData = new PubIndexInput(1L, new PubIndexInput.Data("Magazine", PubType.MAG, "ISSN-1"));

        assertThat(input)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentData);
        assertThat(input.toString()).contains("id=1", "Guide");
    }
}
