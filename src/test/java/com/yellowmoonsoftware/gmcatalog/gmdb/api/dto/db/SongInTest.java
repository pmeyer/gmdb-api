package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SongInTest {

    @Test
    void exposesRecordValuesAndSupportsRecordEquality() {
        final SongDetails details = new SongDetails(3);
        final SongIn song = new SongIn(1L, "Opener", details, 2L);
        final SongIn sameValues = new SongIn(1L, "Opener", details, 2L);
        final SongIn differentTitle = new SongIn(1L, "Closer", details, 2L);

        assertThat(song.id()).isEqualTo(1L);
        assertThat(song.title()).isEqualTo("Opener");
        assertThat(song.details()).isSameAs(details);
        assertThat(song.albumId()).isEqualTo(2L);
        assertThat(song)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentTitle);
        assertThat(song.toString()).contains("id=1", "title=Opener");
    }
}
