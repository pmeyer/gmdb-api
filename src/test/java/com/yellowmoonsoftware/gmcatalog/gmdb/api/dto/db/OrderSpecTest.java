package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderSpecTest {

    @Test
    void equalityUsesOnlyColumnWhenEnumTypeMatches() {
        final OrderSpec<FirstColumn> ascending = new OrderSpec<>(FirstColumn.NAME, OrderByDirection.ASC, OrderByNulls.NULLS_FIRST);
        final OrderSpec<FirstColumn> descending = new OrderSpec<>(FirstColumn.NAME, OrderByDirection.DESC, OrderByNulls.NULLS_LAST);

        assertThat(ascending)
            .isEqualTo(descending)
            .hasSameHashCodeAs(descending);
    }

    @Test
    void equalityRejectsDifferentColumnsInSameEnumType() {
        final OrderSpec<FirstColumn> name = new OrderSpec<>(FirstColumn.NAME, OrderByDirection.ASC, OrderByNulls.NULLS_FIRST);
        final OrderSpec<FirstColumn> title = new OrderSpec<>(FirstColumn.TITLE, OrderByDirection.ASC, OrderByNulls.NULLS_FIRST);

        assertThat(name).isNotEqualTo(title);
    }

    @Test
    void equalityRejectsSameEnumNameFromDifferentEnumType() {
        final OrderSpec<FirstColumn> first = new OrderSpec<>(FirstColumn.NAME, OrderByDirection.ASC, OrderByNulls.NULLS_FIRST);
        final OrderSpec<SecondColumn> second = new OrderSpec<>(SecondColumn.NAME, OrderByDirection.ASC, OrderByNulls.NULLS_FIRST);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equalityHandlesSelfNullAndOtherTypes() {
        final OrderSpec<FirstColumn> spec = new OrderSpec<>(FirstColumn.NAME, null, null);

        assertThat(spec)
            .isEqualTo(spec)
            .isNotEqualTo(null)
            .isNotEqualTo("NAME");
        assertThat(spec.column()).isEqualTo(FirstColumn.NAME);
        assertThat(spec.direction()).isNull();
        assertThat(spec.nullsOrder()).isNull();
    }

    private enum FirstColumn {
        NAME,
        TITLE
    }

    private enum SecondColumn {
        NAME
    }
}
