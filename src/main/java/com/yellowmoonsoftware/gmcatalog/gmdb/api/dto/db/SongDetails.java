package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AbstractResourceBundle;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SongDetails extends AbstractResourceBundle {
    private final Integer trackNumber;

    public SongDetails(Integer trackNumber) {
        super(null);
        this.trackNumber = trackNumber;
    }
}
