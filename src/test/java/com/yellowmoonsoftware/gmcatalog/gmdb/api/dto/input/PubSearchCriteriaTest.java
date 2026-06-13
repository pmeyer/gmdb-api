package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PubSearchCriteriaTest {

    @Test
    void exposesRecordValues() {
        final LocalDate dateStart = LocalDate.of(2020, 1, 1);
        final LocalDate dateEnd = LocalDate.of(2024, 1, 1);

        final PubSearchCriteria criteria = new PubSearchCriteria(1L, 2L, "guide", PubType.BOOK, dateStart, dateEnd, true);

        assertThat(criteria.id()).isEqualTo(1L);
        assertThat(criteria.pubIndexId()).isEqualTo(2L);
        assertThat(criteria.searchName()).isEqualTo("guide");
        assertThat(criteria.type()).isEqualTo(PubType.BOOK);
        assertThat(criteria.dateStart()).isEqualTo(dateStart);
        assertThat(criteria.dateEnd()).isEqualTo(dateEnd);
        assertThat(criteria.hasTranscriptions()).isTrue();
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        final PubSearchCriteria criteria = new PubSearchCriteria(1L, 2L, "guide", PubType.BOOK, LocalDate.of(2020, 1, 1), LocalDate.of(2024, 1, 1), true);
        final PubSearchCriteria sameValues = new PubSearchCriteria(1L, 2L, "guide", PubType.BOOK, LocalDate.of(2020, 1, 1), LocalDate.of(2024, 1, 1), true);
        final PubSearchCriteria differentPubIndex = new PubSearchCriteria(1L, 3L, "guide", PubType.BOOK, LocalDate.of(2020, 1, 1), LocalDate.of(2024, 1, 1), true);

        assertThat(criteria)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentPubIndex);
        assertThat(criteria.toString()).contains("id=1", "pubIndexId=2", "searchName=guide", "hasTranscriptions=true");
    }

    @Test
    void supportsPublicationIndexOnlyCriteria() {
        final PubSearchCriteria criteria = new PubSearchCriteria(null, 2L, null, null, null, null, null);

        assertThat(criteria.id()).isNull();
        assertThat(criteria.pubIndexId()).isEqualTo(2L);
        assertThat(criteria.searchName()).isNull();
    }
}
