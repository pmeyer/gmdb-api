package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistSearchRole;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.OrderByDirection;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.OrderByNulls;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.OrderSpec;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ArtistSearchCriteriaTest {

    @Test
    void exposesRecordValues() {
        final Set<ArtistSearchRole> roles = Set.of(ArtistSearchRole.PERFORMED_BY);
        final LinkedHashSet<OrderSpec<ArtistSearchCriteria.OrderBy>> orderBy = new LinkedHashSet<>();
        orderBy.add(new OrderSpec<>(ArtistSearchCriteria.OrderBy.NAME_TITLE_SORT, OrderByDirection.ASC, OrderByNulls.NULLS_LAST));

        final ArtistSearchCriteria criteria = new ArtistSearchCriteria("alice", ArtistType.PERSON, roles, true, orderBy);

        assertThat(criteria.searchName()).isEqualTo("alice");
        assertThat(criteria.type()).isEqualTo(ArtistType.PERSON);
        assertThat(criteria.roles()).isSameAs(roles);
        assertThat(criteria.restrictToTranscribedArtists()).isTrue();
        assertThat(criteria.orderBy()).isSameAs(orderBy);
    }

    @Test
    void orderByEnumContainsSupportedColumns() {
        assertThat(ArtistSearchCriteria.OrderBy.values())
            .containsExactly(
                ArtistSearchCriteria.OrderBy.NAME,
                ArtistSearchCriteria.OrderBy.NAME_TITLE_SORT,
                ArtistSearchCriteria.OrderBy.TYPE
            );
        assertThat(ArtistSearchCriteria.OrderBy.valueOf("TYPE")).isEqualTo(ArtistSearchCriteria.OrderBy.TYPE);
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        final ArtistSearchCriteria criteria = new ArtistSearchCriteria("alice", ArtistType.PERSON, Set.of(ArtistSearchRole.PERFORMED_BY), true, new LinkedHashSet<>());
        final ArtistSearchCriteria sameValues = new ArtistSearchCriteria("alice", ArtistType.PERSON, Set.of(ArtistSearchRole.PERFORMED_BY), true, new LinkedHashSet<>());
        final ArtistSearchCriteria differentType = new ArtistSearchCriteria("alice", ArtistType.BAND, Set.of(ArtistSearchRole.PERFORMED_BY), true, new LinkedHashSet<>());

        assertThat(criteria)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentType);
        assertThat(criteria.toString()).contains("searchName=alice", "restrictToTranscribedArtists=true");
    }
}
