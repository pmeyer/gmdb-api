package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.BookDetails;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PubOutTest {

    @Test
    void exposesPublicationFieldsAndAction() {
        LocalDate pubDate = LocalDate.of(2024, 1, 15);
        BookDetails details = new BookDetails("First");

        PubOut publication = new PubOut(1L, pubDate, 2L, details, MergeAction.INSERT);

        assertThat(publication.id()).isEqualTo(1L);
        assertThat(publication.pubDate()).isEqualTo(pubDate);
        assertThat(publication.pubIndexId()).isEqualTo(2L);
        assertThat(publication.details()).isSameAs(details);
        assertThat(publication.action()).isEqualTo(MergeAction.INSERT);
    }
}
