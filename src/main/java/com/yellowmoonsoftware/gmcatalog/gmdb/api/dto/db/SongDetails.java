package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AbstractResourceBundle;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SongDetails extends AbstractResourceBundle {
    @JsonProperty("trackNumber")
    private final Integer trackNumber;

    @JsonCreator
    public SongDetails(Integer trackNumber) {
        super(null);
        this.trackNumber = trackNumber;
    }
}
