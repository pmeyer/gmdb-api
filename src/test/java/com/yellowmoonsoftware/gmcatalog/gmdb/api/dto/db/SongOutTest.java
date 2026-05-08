package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SongOutTest {

    @Test
    void exposesSongFieldsAndAction() {
        SongDetails details = new SongDetails(3);

        SongOut song = new SongOut(1L, "Opener", details, 2L, MergeAction.UPDATE);

        assertThat(song.id()).isEqualTo(1L);
        assertThat(song.title()).isEqualTo("Opener");
        assertThat(song.details()).isSameAs(details);
        assertThat(song.albumId()).isEqualTo(2L);
        assertThat(song.action()).isEqualTo(MergeAction.UPDATE);
    }
}
