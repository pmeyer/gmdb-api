package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MergeActionTest {

    @Test
    void definesSupportedMergeActionsInDeclarationOrder() {
        assertThat(MergeAction.values())
                .containsExactly(MergeAction.INSERT, MergeAction.UPDATE, MergeAction.DELETE);
    }

    @Test
    void resolvesMergeActionByName() {
        assertThat(MergeAction.valueOf("INSERT")).isEqualTo(MergeAction.INSERT);
        assertThat(MergeAction.valueOf("UPDATE")).isEqualTo(MergeAction.UPDATE);
        assertThat(MergeAction.valueOf("DELETE")).isEqualTo(MergeAction.DELETE);
    }
}
