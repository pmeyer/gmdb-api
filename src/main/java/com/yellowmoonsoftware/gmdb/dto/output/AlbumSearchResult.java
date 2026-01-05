package com.yellowmoonsoftware.gmdb.dto.output;

import java.time.LocalDate;

public record AlbumSearchResult(
    Long id,
    String title,
    LocalDate releaseDate,
    String albumArtUrl,
    Artist artist
) { }
