package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdAndDataContainerTest {

    private record TestContainer(Long id, String data) implements IdAndDataContainer<String> {
    }

    @Test
    void modeIsAddWhenIdIsNull() {
        assertThat(new TestContainer(null, "data").mode()).isEqualTo(IdAndDataContainer.DataMode.ADD);
    }

    @Test
    void modeIsRefWhenIdIsPresentAndDataIsNull() {
        assertThat(new TestContainer(1L, null).mode()).isEqualTo(IdAndDataContainer.DataMode.REF);
    }

    @Test
    void modeIsUpdateWhenIdAndDataArePresent() {
        assertThat(new TestContainer(1L, "data").mode()).isEqualTo(IdAndDataContainer.DataMode.UPDATE);
    }
}
