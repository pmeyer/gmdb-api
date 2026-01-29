package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class SongOut extends UpsertResult {
    private final Long id;
    private final String title;
    private final SongDetails details;
    private final Long albumId;

    public SongOut(final Long id, final String title, final SongDetails details, final Long albumId, final MergeAction action) {
        super(action);
        this.id = id;
        this.title = title;
        this.details = details;
        this.albumId = albumId;
    }
}


