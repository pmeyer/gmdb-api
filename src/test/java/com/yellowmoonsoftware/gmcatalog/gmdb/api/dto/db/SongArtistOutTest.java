package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SongArtistOutTest {

    @Test
    void exposesInheritedFieldsAndAction() {
        SongArtistRole[] roles = {SongArtistRole.WORDS_BY, SongArtistRole.MUSIC_BY};

        SongArtistOut artist = new SongArtistOut(1L, 2L, roles, MergeAction.UPDATE);

        assertThat(artist.songId()).isEqualTo(1L);
        assertThat(artist.artistId()).isEqualTo(2L);
        assertThat(artist.roles()).isSameAs(roles);
        assertThat(artist.action()).isEqualTo(MergeAction.UPDATE);
    }
}
