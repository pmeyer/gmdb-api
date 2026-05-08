package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SongArtistTest {

    private static final SongArtistRole[] ROLES = {SongArtistRole.MUSIC_BY, SongArtistRole.PERFORMED_BY};

    @Test
    void exposesSongArtistValues() {
        final SongArtist artist = new SongArtist(1L, "Rush", ArtistType.BAND, 10L, ROLES);

        assertThat(artist.id()).isEqualTo(1L);
        assertThat(artist.name()).isEqualTo("Rush");
        assertThat(artist.type()).isEqualTo(ArtistType.BAND);
        assertThat(artist.songId()).isEqualTo(10L);
        assertThat(artist.roles()).containsExactly(SongArtistRole.MUSIC_BY, SongArtistRole.PERFORMED_BY);
    }

    @Test
    void equalityUsesArtistIdAndSongId() {
        final SongArtist artist = new SongArtist(1L, "Rush", ArtistType.BAND, 10L, ROLES);
        final SongArtist sameIds = new SongArtist(1L, "Different", ArtistType.PERSON, 10L, new SongArtistRole[]{SongArtistRole.WORDS_BY});
        final SongArtist differentSongId = new SongArtist(1L, "Rush", ArtistType.BAND, 11L, ROLES);
        final SongArtist differentArtistId = new SongArtist(2L, "Rush", ArtistType.BAND, 10L, ROLES);

        assertThat(artist)
                .isEqualTo(sameIds)
                .hasSameHashCodeAs(sameIds)
                .isNotEqualTo(differentSongId)
                .isNotEqualTo(differentArtistId);
    }

    @Test
    void toStringIncludesBaseAndSongArtistValues() {
        final SongArtist artist = new SongArtist(1L, "Rush", ArtistType.BAND, 10L, ROLES);

        assertThat(artist.toString())
                .startsWith("SongArtist(super=ArtistBase(id=1, name=Rush, type=BAND), songId=10, roles=");
    }
}
