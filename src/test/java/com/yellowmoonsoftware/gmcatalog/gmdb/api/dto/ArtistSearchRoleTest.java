package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArtistSearchRoleTest {

    @Test
    void definesSupportedArtistSearchRolesInDeclarationOrder() {
        assertThat(ArtistSearchRole.values())
                .containsExactly(ArtistSearchRole.WORDS_BY,
                        ArtistSearchRole.MUSIC_BY,
                        ArtistSearchRole.PERFORMED_BY,
                        ArtistSearchRole.ALBUM_ARTIST);
    }

    @Test
    void resolvesArtistSearchRoleByName() {
        assertThat(ArtistSearchRole.valueOf("WORDS_BY")).isEqualTo(ArtistSearchRole.WORDS_BY);
        assertThat(ArtistSearchRole.valueOf("MUSIC_BY")).isEqualTo(ArtistSearchRole.MUSIC_BY);
        assertThat(ArtistSearchRole.valueOf("PERFORMED_BY")).isEqualTo(ArtistSearchRole.PERFORMED_BY);
        assertThat(ArtistSearchRole.valueOf("ALBUM_ARTIST")).isEqualTo(ArtistSearchRole.ALBUM_ARTIST);
    }
}
