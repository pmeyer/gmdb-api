package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.ConditionalNotNull;

@ConditionalNotNull(value = "id", ifNull = "name", message = "TranscriberInput must have an ID or data (or both)")
public record TranscriberInput(Long id, String name) implements IdAndDataContainer<String> {
    @Override
    public String data() {
        return name;
    }
}
