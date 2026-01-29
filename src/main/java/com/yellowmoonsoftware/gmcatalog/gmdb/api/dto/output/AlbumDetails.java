package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Getter
@Accessors(fluent = true)
public class AlbumDetails extends AbstractResourceBundle {
    private final LocalDate releaseDate;

    @Getter(lazy = true)
    private final String albumArtUrl = getResourceUrl(ResourceSlug.ALBUM_ART).orElse(null);

    public AlbumDetails(final LocalDate releaseDate) {
        super(null);
        this.releaseDate = releaseDate;
    }
}

