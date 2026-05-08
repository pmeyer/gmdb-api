package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookDetailsTest {

    @Test
    void exposesBookEditionAndInheritedResourceState() {
        final BookDetails details = new BookDetails("First Edition");

        assertThat(details.edition()).isEqualTo("First Edition");
        assertThat(details.resourceId()).isNull();
        assertThat(details.resources()).isEmpty();
    }

    @Test
    void coverIsNullWhenNoCoverImageResourceExists() {
        final BookDetails details = new BookDetails("First Edition");

        assertThat(details.cover()).isNull();
    }

    @Test
    void toStringIncludesEdition() {
        final BookDetails details = new BookDetails("First Edition");

        assertThat(details).hasToString("BookDetails(edition=First Edition)");
    }
}
