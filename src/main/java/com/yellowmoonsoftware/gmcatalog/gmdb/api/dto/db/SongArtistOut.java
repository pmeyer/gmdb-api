package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.type.JsonTypeHandler;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class SongArtistOut extends SongArtistIn {
    private final MergeAction action;

    public SongArtistOut(final Long songId, final Long artistId, @JsonTypeHandler final SongArtistRole[] roles, final MergeAction action) {
        super(songId, artistId, roles);
        this.action = action;
    }
}
