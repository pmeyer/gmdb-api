package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderByNullsTest {

    @Test
    void definesSupportedNullOrderingInDeclarationOrder() {
        assertThat(OrderByNulls.values()).containsExactly(OrderByNulls.NULLS_FIRST, OrderByNulls.NULLS_LAST);
    }

    @Test
    void resolvesNullOrderingByName() {
        assertThat(OrderByNulls.valueOf("NULLS_FIRST")).isEqualTo(OrderByNulls.NULLS_FIRST);
        assertThat(OrderByNulls.valueOf("NULLS_LAST")).isEqualTo(OrderByNulls.NULLS_LAST);
    }

    @Test
    void exposesSqlClauseText() {
        assertThat(OrderByNulls.NULLS_FIRST.clause()).isEqualTo("NULLS FIRST");
        assertThat(OrderByNulls.NULLS_LAST.clause()).isEqualTo("NULLS LAST");
    }
}
