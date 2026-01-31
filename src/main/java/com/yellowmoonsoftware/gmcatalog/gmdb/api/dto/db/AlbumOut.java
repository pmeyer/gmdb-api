package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.AlbumDetails;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class AlbumOut extends UpsertResult {
    private final Long id;
    private final String title;
    private final AlbumDetails details;
    private final Long primaryArtistId;

    public AlbumOut(final Long id, final String title, final AlbumDetails details, final Long primaryArtistId, final MergeAction action) {
        super(action);
        this.id = id;
        this.title = title;
        this.details = details;
        this.primaryArtistId = primaryArtistId;
    }
}
