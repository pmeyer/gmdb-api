package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistSearchRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.type.JsonTypeHandler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@ToString(callSuper = true)
@Accessors(fluent = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ArtistSearchResult extends ArtistBase {
    private final Set<ArtistSearchRole> matchedRoles;

    public ArtistSearchResult(Long id, String name, ArtistType type, @JsonTypeHandler ArtistSearchRole[] matchedRoles) {
        this(id, name, type, Set.of(matchedRoles));
    }

    public ArtistSearchResult(Long id, String name, ArtistType type, Set<ArtistSearchRole> matchedRoles) {
        super(id, name, type);
        this.matchedRoles = matchedRoles;
    }
}

