package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.ConditionalNotNull;
import jakarta.validation.Valid;

import java.util.Set;

@ConditionalNotNull(value = "id", ifNull = "data", message = "SongArtistInput must have an ID or data")
public record SongArtistInput(
        Long id,
        @Valid ArtistData data,
        Set<SongArtistRole> roles
) implements IdAndDataContainer<ArtistData> { }
