package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SongArtistRoleTest {

    @Test
    void definesSupportedSongArtistRolesInDeclarationOrder() {
        assertThat(SongArtistRole.values())
                .containsExactly(SongArtistRole.WORDS_BY, SongArtistRole.MUSIC_BY, SongArtistRole.PERFORMED_BY);
    }

    @Test
    void resolvesSongArtistRoleByName() {
        assertThat(SongArtistRole.valueOf("WORDS_BY")).isEqualTo(SongArtistRole.WORDS_BY);
        assertThat(SongArtistRole.valueOf("MUSIC_BY")).isEqualTo(SongArtistRole.MUSIC_BY);
        assertThat(SongArtistRole.valueOf("PERFORMED_BY")).isEqualTo(SongArtistRole.PERFORMED_BY);
    }
}
