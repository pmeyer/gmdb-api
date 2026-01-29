package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.ConditionalNotNull;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;

@ConditionalNotNull(value = "id", ifNull = "data", message = "PubIndexInput must have an ID or data")
public record PubIndexInput(Long id, PubIndexInput.Data data) implements IdAndDataContainer<PubIndexInput.Data> {

    public record Data(
            String name,
            PubType type,
            String serial
    ) { }
}
