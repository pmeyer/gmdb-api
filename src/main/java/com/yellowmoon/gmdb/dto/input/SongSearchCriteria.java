package com.yellowmoon.gmdb.dto.input;

import com.yellowmoon.gmdb.dto.ArtistSearchRole;

import java.util.Objects;
import java.util.Set;

public record SongSearchCriteria(
        String titleSearch,
        String pubNameSearch,
        String artistSearch,
        String albumSearch,
        Set<ArtistMatchCriteria> artists,
        Set<Long> albums,
        Set<Long> pubs
) {

    public record ArtistMatchCriteria(
        Long id,
        Set<ArtistSearchRole> roles
    ) {
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ArtistMatchCriteria other)) {
                return false;
            }
            return Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
