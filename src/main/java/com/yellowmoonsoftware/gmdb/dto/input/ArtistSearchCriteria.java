package com.yellowmoonsoftware.gmdb.dto.input;

import com.yellowmoonsoftware.gmdb.dto.ArtistSearchRole;
import com.yellowmoonsoftware.gmdb.dto.ArtistType;

import java.util.Set;

public record ArtistSearchCriteria(
        String searchName,
        ArtistType type,
        Set<ArtistSearchRole> roles
) { }

