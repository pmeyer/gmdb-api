package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class ArtistOut extends UpsertResult {
    private final Long id;
    private final String name;
    private final ArtistType type;

    public ArtistOut(final Long id, final String name, final ArtistType type, final MergeAction action) {
        super(action);
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
