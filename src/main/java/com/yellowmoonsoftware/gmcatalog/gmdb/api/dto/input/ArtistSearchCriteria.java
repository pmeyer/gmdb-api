package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistSearchRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;

import java.util.Set;

public record ArtistSearchCriteria(
        String searchName,
        ArtistType type,
        Set<ArtistSearchRole> roles
) { }

