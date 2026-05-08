package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PubSearchCriteriaTest {

    @Test
    void exposesRecordValues() {
        LocalDate dateStart = LocalDate.of(2020, 1, 1);
        LocalDate dateEnd = LocalDate.of(2024, 1, 1);

        PubSearchCriteria criteria = new PubSearchCriteria("guide", PubType.BOOK, dateStart, dateEnd, true);

        assertThat(criteria.searchName()).isEqualTo("guide");
        assertThat(criteria.type()).isEqualTo(PubType.BOOK);
        assertThat(criteria.dateStart()).isEqualTo(dateStart);
        assertThat(criteria.dateEnd()).isEqualTo(dateEnd);
        assertThat(criteria.hasTranscriptions()).isTrue();
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        PubSearchCriteria criteria = new PubSearchCriteria("guide", PubType.BOOK, LocalDate.of(2020, 1, 1), LocalDate.of(2024, 1, 1), true);
        PubSearchCriteria sameValues = new PubSearchCriteria("guide", PubType.BOOK, LocalDate.of(2020, 1, 1), LocalDate.of(2024, 1, 1), true);
        PubSearchCriteria differentType = new PubSearchCriteria("guide", PubType.MAG, LocalDate.of(2020, 1, 1), LocalDate.of(2024, 1, 1), true);

        assertThat(criteria)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentType);
        assertThat(criteria.toString()).contains("searchName=guide", "hasTranscriptions=true");
    }
}
