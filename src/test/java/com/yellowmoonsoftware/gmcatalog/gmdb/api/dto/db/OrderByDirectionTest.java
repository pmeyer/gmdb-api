package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderByDirectionTest {

    @Test
    void definesSupportedDirectionsInDeclarationOrder() {
        assertThat(OrderByDirection.values()).containsExactly(OrderByDirection.ASC, OrderByDirection.DESC);
    }

    @Test
    void resolvesDirectionByName() {
        assertThat(OrderByDirection.valueOf("ASC")).isEqualTo(OrderByDirection.ASC);
        assertThat(OrderByDirection.valueOf("DESC")).isEqualTo(OrderByDirection.DESC);
    }

    @Test
    void exposesSqlClauseText() {
        assertThat(OrderByDirection.ASC.clause()).isEqualTo("ASC");
        assertThat(OrderByDirection.DESC.clause()).isEqualTo("DESC");
    }
}
