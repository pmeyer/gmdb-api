package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PubIndexOutTest {

    @Test
    void defaultConstructorLeavesActionNull() {
        PubIndexOut index = new PubIndexOut(1L, "Guide", PubType.BOOK, "ISBN-1");

        assertThat(index.id()).isEqualTo(1L);
        assertThat(index.name()).isEqualTo("Guide");
        assertThat(index.type()).isEqualTo(PubType.BOOK);
        assertThat(index.serialNumber()).isEqualTo("ISBN-1");
        assertThat(index.action()).isNull();
    }

    @Test
    void actionConstructorExposesAction() {
        PubIndexOut index = new PubIndexOut(1L, "Guide", PubType.BOOK, "ISBN-1", MergeAction.UPDATE);

        assertThat(index.action()).isEqualTo(MergeAction.UPDATE);
    }
}
