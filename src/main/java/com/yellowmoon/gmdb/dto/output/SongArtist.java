package com.yellowmoon.gmdb.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.yellowmoon.gmdb.dto.ArtistType;
import com.yellowmoon.gmdb.dto.SongArtistRole;
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
    private final Set<SongArtistRole> roles;

    @JsonCreator
    public SongArtist(Long id, String name, ArtistType type, Long songId, Set<SongArtistRole> roles) {
        super(id, name, type);
        this.songId = songId;
        this.roles = roles;
    }
}
