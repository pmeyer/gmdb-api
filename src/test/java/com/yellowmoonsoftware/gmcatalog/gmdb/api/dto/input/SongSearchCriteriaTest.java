package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistSearchRole;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SongSearchCriteriaTest {

    @Test
    void exposesRecordValues() {
        final Set<SongSearchCriteria.ArtistMatchCriteria> artists = Set.of(new SongSearchCriteria.ArtistMatchCriteria(1L, Set.of(ArtistSearchRole.PERFORMED_BY)));
        final Set<Long> albums = Set.of(2L);
        final Set<Long> pubs = Set.of(3L);

        final SongSearchCriteria criteria = new SongSearchCriteria("song", "pub", "artist", "album", artists, albums, pubs);

        assertThat(criteria.titleSearch()).isEqualTo("song");
        assertThat(criteria.pubNameSearch()).isEqualTo("pub");
        assertThat(criteria.artistSearch()).isEqualTo("artist");
        assertThat(criteria.albumSearch()).isEqualTo("album");
        assertThat(criteria.artists()).isSameAs(artists);
        assertThat(criteria.albums()).isSameAs(albums);
        assertThat(criteria.pubs()).isSameAs(pubs);
    }

    @Test
    void artistMatchCriteriaExposesValues() {
        final Set<ArtistSearchRole> roles = Set.of(ArtistSearchRole.WORDS_BY);

        final SongSearchCriteria.ArtistMatchCriteria criteria = new SongSearchCriteria.ArtistMatchCriteria(1L, roles);

        assertThat(criteria.id()).isEqualTo(1L);
        assertThat(criteria.roles()).isSameAs(roles);
    }

    @Test
    void artistMatchCriteriaEqualityUsesOnlyId() {
        final SongSearchCriteria.ArtistMatchCriteria criteria = new SongSearchCriteria.ArtistMatchCriteria(1L, Set.of(ArtistSearchRole.WORDS_BY));
        final SongSearchCriteria.ArtistMatchCriteria sameId = new SongSearchCriteria.ArtistMatchCriteria(1L, Set.of(ArtistSearchRole.MUSIC_BY));
        final SongSearchCriteria.ArtistMatchCriteria differentId = new SongSearchCriteria.ArtistMatchCriteria(2L, Set.of(ArtistSearchRole.WORDS_BY));

        assertThat(criteria)
            .isEqualTo(sameId)
            .hasSameHashCodeAs(sameId)
            .isNotEqualTo(differentId)
            .isNotEqualTo("1");
    }

    @Test
    void artistMatchCriteriaEqualityHandlesNullIds() {
        final SongSearchCriteria.ArtistMatchCriteria criteria = new SongSearchCriteria.ArtistMatchCriteria(null, Set.of(ArtistSearchRole.WORDS_BY));
        final SongSearchCriteria.ArtistMatchCriteria sameNullId = new SongSearchCriteria.ArtistMatchCriteria(null, Set.of(ArtistSearchRole.MUSIC_BY));

        assertThat(criteria)
            .isEqualTo(sameNullId)
            .hasSameHashCodeAs(sameNullId);
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        final SongSearchCriteria criteria = new SongSearchCriteria("song", "pub", "artist", "album", Set.of(), Set.of(2L), Set.of(3L));
        final SongSearchCriteria sameValues = new SongSearchCriteria("song", "pub", "artist", "album", Set.of(), Set.of(2L), Set.of(3L));
        final SongSearchCriteria differentTitle = new SongSearchCriteria("other", "pub", "artist", "album", Set.of(), Set.of(2L), Set.of(3L));

        assertThat(criteria)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentTitle);
        assertThat(criteria.toString()).contains("titleSearch=song", "albumSearch=album");
    }
}
