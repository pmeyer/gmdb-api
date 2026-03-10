package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistSearchRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.OrderSpec;

import java.util.LinkedHashSet;
import java.util.Set;

public record ArtistSearchCriteria(
        String searchName,
        ArtistType type,
        Set<ArtistSearchRole> roles,
        Boolean restrictToTranscribedArtists,
        LinkedHashSet<OrderSpec<ArtistSearchCriteria.OrderBy>> orderBy
) {
    enum OrderBy {
        /**
         * Sort by artist name
         */
        NAME,
        /**
         * Sort by artist name, ignoring articles (The, A, An, etc.)
         */
        NAME_TITLE_SORT,
        /**
         * Sort by artist type
         */
        TYPE
    }
}

