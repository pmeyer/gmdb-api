package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PubIndexCriteriaTest {

    @Test
    void exposesRecordValues() {
        final PubIndexCriteria criteria = new PubIndexCriteria(PubType.BOOK);

        assertThat(criteria.type()).isEqualTo(PubType.BOOK);
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        final PubIndexCriteria criteria = new PubIndexCriteria(PubType.BOOK);
        final PubIndexCriteria sameValues = new PubIndexCriteria(PubType.BOOK);
        final PubIndexCriteria differentType = new PubIndexCriteria(PubType.MAG);

        assertThat(criteria)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentType);
        assertThat(criteria.toString()).contains("type=BOOK");
    }
}
