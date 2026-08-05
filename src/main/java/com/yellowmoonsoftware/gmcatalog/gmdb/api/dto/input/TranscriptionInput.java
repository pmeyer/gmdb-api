package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.ConditionalNotNull;
import jakarta.validation.Valid;

@ConditionalNotNull(value = "id", ifNull = "data", message = "TranscriptionInput must have an ID or data")
public record TranscriptionInput(
        Long id,
        @Valid TranscriptionData data
) implements IdAndDataContainer<TranscriptionData> {
}
