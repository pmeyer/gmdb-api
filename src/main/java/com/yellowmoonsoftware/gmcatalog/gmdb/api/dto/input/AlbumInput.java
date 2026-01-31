package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.ConditionalNotNull;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import jakarta.validation.Valid;

@ConditionalNotNull(value = "id", ifNull = "data", message = "AlbumInput must have an ID or data")
public record AlbumInput(
        Long id,
        @Valid AlbumData data
) implements IdAndDataContainer<AlbumData> {
}
