package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.type.JsonTypeHandler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@ToString(callSuper = true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class SongArtist extends ArtistBase {
    @EqualsAndHashCode.Include
    private final Long songId;
    @JsonTypeHandler
    private final SongArtistRole[] roles;

    @JsonCreator
    public SongArtist(Long id, String name, ArtistType type, Long songId, SongArtistRole[] roles) {
        super(id, name, type);
        this.songId = songId;
        this.roles = roles;
    }
}
