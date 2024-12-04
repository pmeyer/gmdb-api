package com.yellowmoon.gmdb.dto.output;

import com.yellowmoon.gmdb.dto.ArtistType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString(callSuper = true)
@Accessors(fluent = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Artist extends ArtistBase {
    public Artist(Long id, String name, ArtistType type) {
        super(id, name, type);
    }
}