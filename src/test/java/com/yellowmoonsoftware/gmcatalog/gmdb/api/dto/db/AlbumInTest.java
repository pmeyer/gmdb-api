package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AlbumData;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AlbumInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistData;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.ArtistInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumDetails;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumInTest {

    @Test
    void exposesRecordValuesAndSupportsRecordEquality() {
        AlbumDetails details = new AlbumDetails(LocalDate.of(2020, 4, 5));
        AlbumIn album = new AlbumIn(1L, "Live Set", details, 2L);
        AlbumIn sameValues = new AlbumIn(1L, "Live Set", details, 2L);
        AlbumIn differentTitle = new AlbumIn(1L, "Studio", details, 2L);

        assertThat(album.id()).isEqualTo(1L);
        assertThat(album.title()).isEqualTo("Live Set");
        assertThat(album.details()).isSameAs(details);
        assertThat(album.primaryArtistId()).isEqualTo(2L);
        assertThat(album)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentTitle);
    }

    @Test
    void fromMapsInputDataAndPrimaryArtist() {
        AlbumInput input = new AlbumInput(1L, albumData());

        AlbumIn album = AlbumIn.from(input, 20L);

        assertThat(album.id()).isEqualTo(1L);
        assertThat(album.title()).isEqualTo("Live Set");
        assertThat(album.details().releaseDate()).isEqualTo(LocalDate.of(2020, 4, 5));
        assertThat(album.primaryArtistId()).isEqualTo(20L);
    }

    @Test
    void fromHandlesReferenceOnlyInput() {
        AlbumInput input = new AlbumInput(1L, null);

        AlbumIn album = AlbumIn.from(input, 20L);

        assertThat(album.id()).isEqualTo(1L);
        assertThat(album.title()).isNull();
        assertThat(album.details()).isNull();
        assertThat(album.primaryArtistId()).isEqualTo(20L);
    }

    private static AlbumData albumData() {
        ArtistInput artist = new ArtistInput(10L, new ArtistData("Alice", ArtistType.PERSON));
        return new AlbumData("Live Set", null, LocalDate.of(2020, 4, 5), artist);
    }
}
