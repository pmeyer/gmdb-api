package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import javax.naming.directory.SearchResult;
import java.time.LocalDate;

public record AlbumSearchResult(
    Long id,
    String title,
    LocalDate releaseDate,
    String albumArtUrl,
    Long primaryArtistId
) implements HasAlbumPrimaryArtistId { }
