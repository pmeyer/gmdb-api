package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumSearchResultTest {

    @Test
    void exposesRecordValuesAndPrimaryArtistContract() {
        final LocalDate releaseDate = LocalDate.of(2023, 8, 1);

        final AlbumSearchResult result = new AlbumSearchResult(
            1L,
            "Live Set",
            releaseDate,
            "/resources/albums/live.jpg",
            20L
        );

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Live Set");
        assertThat(result.releaseDate()).isEqualTo(releaseDate);
        assertThat(result.albumArtUrl()).isEqualTo("/resources/albums/live.jpg");
        assertThat(result.primaryArtistId()).isEqualTo(20L);
        assertThat((HasAlbumPrimaryArtistId) result).extracting(HasAlbumPrimaryArtistId::primaryArtistId)
            .isEqualTo(20L);
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        final AlbumSearchResult result = album();
        final AlbumSearchResult sameValues = album();
        final AlbumSearchResult differentTitle = new AlbumSearchResult(1L, "Studio", LocalDate.of(2023, 8, 1), "art.jpg", 20L);

        assertThat(result)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentTitle);
        assertThat(result.toString()).contains("Live Set", "primaryArtistId=20");
    }

    private static AlbumSearchResult album() {
        return new AlbumSearchResult(1L, "Live Set", LocalDate.of(2023, 8, 1), "art.jpg", 20L);
    }
}
