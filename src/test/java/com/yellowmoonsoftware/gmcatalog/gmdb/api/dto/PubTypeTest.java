package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PubTypeTest {

    @Test
    void definesSupportedPublicationTypesInDeclarationOrder() {
        assertThat(PubType.values()).containsExactly(PubType.BOOK, PubType.MAG);
    }

    @Test
    void resolvesPublicationTypeByName() {
        assertThat(PubType.valueOf("BOOK")).isEqualTo(PubType.BOOK);
        assertThat(PubType.valueOf("MAG")).isEqualTo(PubType.MAG);
    }
}
