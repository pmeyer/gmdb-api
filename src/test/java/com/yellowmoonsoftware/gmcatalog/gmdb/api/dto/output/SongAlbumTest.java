package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.MergeAction;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceAttributes;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class SongAlbumTest {

    @Test
    void exposesRecordValuesAndPrimaryArtistContract() {
        LocalDate releaseDate = LocalDate.of(2020, 4, 5);

        SongAlbum album = new SongAlbum(1L, "Live Set", 3, releaseDate, "art.jpg", 10L);

        assertThat(album.id()).isEqualTo(1L);
        assertThat(album.title()).isEqualTo("Live Set");
        assertThat(album.trackNumber()).isEqualTo(3);
        assertThat(album.releaseDate()).isEqualTo(releaseDate);
        assertThat(album.albumArtUrl()).isEqualTo("art.jpg");
        assertThat(album.primaryArtistId()).isEqualTo(10L);
        assertThat((HasAlbumPrimaryArtistId) album).extracting(HasAlbumPrimaryArtistId::primaryArtistId)
            .isEqualTo(10L);
    }

    @Test
    void derivesValuesFromAlbumAndSong() {
        AlbumDetails details = new AlbumDetails(LocalDate.of(2020, 4, 5));
        details.resources().put(ResourceSlug.ALBUM_ART, new ResourceAttributes("cover.jpg", MediaType.IMAGE_JPEG));
        AlbumOut albumOut = new AlbumOut(1L, "Live Set", details, 10L, MergeAction.INSERT);
        SongSearchResult song = new SongSearchResult(20L, "Opener", 3, 1L);

        SongAlbum album = new SongAlbum(albumOut, song);

        assertThat(album.id()).isEqualTo(1L);
        assertThat(album.title()).isEqualTo("Live Set");
        assertThat(album.trackNumber()).isEqualTo(3);
        assertThat(album.releaseDate()).isEqualTo(LocalDate.of(2020, 4, 5));
        assertThat(album.albumArtUrl()).isEqualTo(details.albumArtUrl());
        assertThat(album.primaryArtistId()).isEqualTo(10L);
    }

    @Test
    void equalityUsesOnlyId() {
        SongAlbum album = new SongAlbum(1L, "Live Set", 3, LocalDate.of(2020, 4, 5), "art.jpg", 10L);
        SongAlbum sameId = new SongAlbum(1L, "Studio", 4, LocalDate.of(2021, 1, 1), "other.jpg", 11L);
        SongAlbum differentId = new SongAlbum(2L, "Live Set", 3, LocalDate.of(2020, 4, 5), "art.jpg", 10L);

        assertThat(album)
            .isEqualTo(sameId)
            .hasSameHashCodeAs(sameId)
            .isNotEqualTo(differentId);
    }

    @Test
    void equalityRejectsDifferentTypesAndHandlesNullIds() {
        SongAlbum album = new SongAlbum(1L, "Live Set", 3, LocalDate.of(2020, 4, 5), "art.jpg", 10L);
        SongAlbum nullId = new SongAlbum(null, "Live Set", 3, LocalDate.of(2020, 4, 5), "art.jpg", 10L);
        SongAlbum sameNullId = new SongAlbum(null, "Studio", 4, LocalDate.of(2021, 1, 1), "other.jpg", 11L);

        assertThat(album).isNotEqualTo("1");
        assertThat(nullId)
            .isEqualTo(sameNullId)
            .hasSameHashCodeAs(sameNullId);
        assertThat(nullId.hashCode()).isZero();
    }

    @Test
    void toStringIncludesRecordValues() {
        SongAlbum album = new SongAlbum(1L, "Live Set", 3, LocalDate.of(2020, 4, 5), "art.jpg", 10L);

        assertThat(album.toString()).contains("id=1", "title=Live Set", "trackNumber=3");
    }
}
