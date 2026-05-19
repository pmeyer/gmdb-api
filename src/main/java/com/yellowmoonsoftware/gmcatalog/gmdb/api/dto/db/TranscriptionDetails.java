package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AbstractResourceBundle;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.service.ResourceSlug;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranscriptionDetails extends AbstractResourceBundle {
    private final Integer pageNumber;

    @Getter(lazy = true)
    private final String transcriptionUrl = getResourceUrl(ResourceSlug.TRANSCRIPTION).orElse(null);

    @JsonCreator
    public TranscriptionDetails(@JsonProperty("pageNumber") final Integer pageNumber) {
        super(null);
        this.pageNumber = pageNumber;
    }
}
