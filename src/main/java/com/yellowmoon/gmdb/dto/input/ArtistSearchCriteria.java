package com.yellowmoon.gmdb.dto.input;

import com.yellowmoon.gmdb.dto.ArtistSearchRole;
import com.yellowmoon.gmdb.dto.ArtistType;

import java.util.Set;

public record ArtistSearchCriteria(
        String searchName,
        ArtistType type,
        Set<ArtistSearchRole> roles
) { }

