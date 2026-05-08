package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArtistTest {

    @Test
    void exposesArtistValues() {
        final Artist artist = new Artist(1L, "Rush", ArtistType.BAND);

        assertThat(artist.id()).isEqualTo(1L);
        assertThat(artist.name()).isEqualTo("Rush");
        assertThat(artist.type()).isEqualTo(ArtistType.BAND);
    }

    @Test
    void equalityUsesArtistId() {
        final Artist artist = new Artist(1L, "Rush", ArtistType.BAND);
        final Artist sameId = new Artist(1L, "Different", ArtistType.PERSON);
        final Artist differentId = new Artist(2L, "Rush", ArtistType.BAND);

        assertThat(artist)
                .isEqualTo(sameId)
                .hasSameHashCodeAs(sameId)
                .isNotEqualTo(differentId);
    }

    @Test
    void toStringIncludesBaseArtistValues() {
        final Artist artist = new Artist(1L, "Rush", ArtistType.BAND);

        assertThat(artist).hasToString("Artist(super=ArtistBase(id=1, name=Rush, type=BAND))");
    }
}
