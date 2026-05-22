package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Getter
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlbumDetails extends AbstractResourceBundle {
    @JsonProperty
    private final LocalDate releaseDate;

    @Getter(lazy = true)
    private final String albumArtUrl = getResourceUrl(ResourceSlug.ALBUM_ART).orElse(null);

    @JsonCreator
    public AlbumDetails(final LocalDate releaseDate) {
        super(null);
        this.releaseDate = releaseDate;
    }
}
