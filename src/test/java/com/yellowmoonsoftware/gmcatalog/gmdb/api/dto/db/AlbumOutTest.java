package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumDetails;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumOutTest {

    @Test
    void exposesAlbumFieldsAndAction() {
        AlbumDetails details = new AlbumDetails(LocalDate.of(2020, 4, 5));

        AlbumOut album = new AlbumOut(1L, "Live Set", details, 2L, MergeAction.UPDATE);

        assertThat(album.id()).isEqualTo(1L);
        assertThat(album.title()).isEqualTo("Live Set");
        assertThat(album.details()).isSameAs(details);
        assertThat(album.primaryArtistId()).isEqualTo(2L);
        assertThat(album.action()).isEqualTo(MergeAction.UPDATE);
    }
}
