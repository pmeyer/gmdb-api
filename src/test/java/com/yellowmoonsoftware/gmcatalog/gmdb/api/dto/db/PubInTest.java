package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.BookEditionInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.BookInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.PubIndexInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.BookDetails;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PubInTest {

    @Test
    void exposesRecordValuesAndSupportsRecordEquality() {
        LocalDate pubDate = LocalDate.of(2024, 1, 15);
        BookDetails details = new BookDetails("First");
        PubIn publication = new PubIn(1L, pubDate, 2L, details);
        PubIn sameValues = new PubIn(1L, pubDate, 2L, details);
        PubIn differentId = new PubIn(3L, pubDate, 2L, details);

        assertThat(publication.id()).isEqualTo(1L);
        assertThat(publication.pubDate()).isEqualTo(pubDate);
        assertThat(publication.pubIdxId()).isEqualTo(2L);
        assertThat(publication.details()).isSameAs(details);
        assertThat(publication)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentId);
    }

    @Test
    void forNewPubMapsPublicationInput() {
        LocalDate pubDate = LocalDate.of(2024, 1, 15);
        BookInput input = new BookInput(pubDate, new PubIndexInput(1L, null), new BookEditionInput("First", null), List.of());

        PubIn publication = PubIn.forNewPub(2L, input);

        assertThat(publication.id()).isNull();
        assertThat(publication.pubDate()).isEqualTo(pubDate);
        assertThat(publication.pubIdxId()).isEqualTo(2L);
        assertThat(publication.details()).isInstanceOfSatisfying(BookDetails.class,
            details -> assertThat(details.edition()).isEqualTo("First"));
    }
}
