package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.ConditionalNotNull;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import jakarta.validation.Valid;

@ConditionalNotNull(value = "id", ifNull = "data", message = "ArtistInput must have an ID or data")
public record ArtistInput(
        Long id,
        @Valid ArtistData data
) implements IdAndDataContainer<ArtistData> {
}
