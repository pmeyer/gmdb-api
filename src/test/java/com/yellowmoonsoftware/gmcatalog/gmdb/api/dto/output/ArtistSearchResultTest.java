package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistSearchRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ArtistSearchResultTest {

    @Test
    void arrayConstructorExposesArtistFieldsAndMatchedRoles() {
        final ArtistSearchResult result = new ArtistSearchResult(
            1L,
            "The Band",
            ArtistType.BAND,
            new ArtistSearchRole[]{ArtistSearchRole.ALBUM_ARTIST, ArtistSearchRole.PERFORMED_BY}
        );

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("The Band");
        assertThat(result.type()).isEqualTo(ArtistType.BAND);
        assertThat(result.matchedRoles())
            .containsExactlyInAnyOrder(ArtistSearchRole.ALBUM_ARTIST, ArtistSearchRole.PERFORMED_BY);
    }

    @Test
    void setConstructorKeepsProvidedRoles() {
        final Set<ArtistSearchRole> roles = Set.of(ArtistSearchRole.WORDS_BY);

        final ArtistSearchResult result = new ArtistSearchResult(2L, "Alice", ArtistType.PERSON, roles);

        assertThat(result.matchedRoles()).isSameAs(roles);
    }

    @Test
    void equalityUsesArtistBaseIdentityAndIgnoresMatchedRoles() {
        final ArtistSearchResult result = new ArtistSearchResult(1L, "The Band", ArtistType.BAND, Set.of(ArtistSearchRole.PERFORMED_BY));
        final ArtistSearchResult sameId = new ArtistSearchResult(1L, "Different", ArtistType.PERSON, Set.of(ArtistSearchRole.WORDS_BY));
        final ArtistSearchResult sameArtistDifferentRoles = new ArtistSearchResult(1L, "The Band", ArtistType.BAND, Set.of(ArtistSearchRole.ALBUM_ARTIST));
        final ArtistSearchResult differentId = new ArtistSearchResult(2L, "The Band", ArtistType.BAND, Set.of(ArtistSearchRole.PERFORMED_BY));

        assertThat(result)
            .isEqualTo(sameId)
            .hasSameHashCodeAs(sameId)
            .isEqualTo(sameArtistDifferentRoles)
            .hasSameHashCodeAs(sameArtistDifferentRoles)
            .isNotEqualTo(differentId);
    }

    @Test
    void toStringIncludesArtistAndRoleState() {
        final ArtistSearchResult result = new ArtistSearchResult(1L, "The Band", ArtistType.BAND, Set.of(ArtistSearchRole.PERFORMED_BY));

        assertThat(result.toString())
            .contains("id=1")
            .contains("name=The Band")
            .contains("type=BAND")
            .contains("matchedRoles=[PERFORMED_BY]");
    }
}
