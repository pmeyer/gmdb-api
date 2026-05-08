package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArtistOutTest {

    @Test
    void exposesArtistFieldsAndAction() {
        ArtistOut artist = new ArtistOut(1L, "Alice", ArtistType.PERSON, MergeAction.INSERT);

        assertThat(artist.id()).isEqualTo(1L);
        assertThat(artist.name()).isEqualTo("Alice");
        assertThat(artist.type()).isEqualTo(ArtistType.PERSON);
        assertThat(artist.action()).isEqualTo(MergeAction.INSERT);
    }
}
