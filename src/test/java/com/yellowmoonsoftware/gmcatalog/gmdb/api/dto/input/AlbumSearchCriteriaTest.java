package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.OrderByDirection;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.OrderByNulls;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.OrderSpec;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumSearchCriteriaTest {

    @Test
    void exposesRecordValues() {
        LinkedHashSet<OrderSpec<AlbumSearchCriteria.OrderBy>> orderBy = new LinkedHashSet<>();
        orderBy.add(new OrderSpec<>(AlbumSearchCriteria.OrderBy.RELEASE_DATE, OrderByDirection.DESC, OrderByNulls.NULLS_LAST));
        LocalDate dateStart = LocalDate.of(2020, 1, 1);
        LocalDate dateEnd = LocalDate.of(2024, 1, 1);

        AlbumSearchCriteria criteria = new AlbumSearchCriteria("live", dateStart, dateEnd, orderBy);

        assertThat(criteria.searchName()).isEqualTo("live");
        assertThat(criteria.dateStart()).isEqualTo(dateStart);
        assertThat(criteria.dateEnd()).isEqualTo(dateEnd);
        assertThat(criteria.orderBy()).isSameAs(orderBy);
    }

    @Test
    void orderByEnumContainsSupportedColumns() {
        assertThat(AlbumSearchCriteria.OrderBy.values())
            .containsExactly(
                AlbumSearchCriteria.OrderBy.RELEASE_DATE,
                AlbumSearchCriteria.OrderBy.TITLE,
                AlbumSearchCriteria.OrderBy.ARTIST
            );
        assertThat(AlbumSearchCriteria.OrderBy.valueOf("TITLE")).isEqualTo(AlbumSearchCriteria.OrderBy.TITLE);
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        AlbumSearchCriteria criteria = new AlbumSearchCriteria("live", LocalDate.of(2020, 1, 1), LocalDate.of(2024, 1, 1), new LinkedHashSet<>());
        AlbumSearchCriteria sameValues = new AlbumSearchCriteria("live", LocalDate.of(2020, 1, 1), LocalDate.of(2024, 1, 1), new LinkedHashSet<>());
        AlbumSearchCriteria differentSearch = new AlbumSearchCriteria("studio", LocalDate.of(2020, 1, 1), LocalDate.of(2024, 1, 1), new LinkedHashSet<>());

        assertThat(criteria)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentSearch);
        assertThat(criteria.toString()).contains("searchName=live");
    }
}
