package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import java.time.LocalDate;
import java.util.Objects;

public record SongAlbum(
        Long id,
        String title,
        Integer trackNumber,
        LocalDate releaseDate,
        String albumArtUrl,
        Artist artist
) {

    public SongAlbum(AlbumSearchResult album, SongSearchResult song) {
        this(album.id(), album.title(), song.trackNumber(),
                album.releaseDate(), album.albumArtUrl(), album.artist());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SongAlbum other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
