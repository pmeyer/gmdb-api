package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.SongArtistRole;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SongArtistInputTest {

    @Test
    void exposesRecordValuesAndDataContract() {
        ArtistData data = new ArtistData("Alice", ArtistType.PERSON);
        Set<SongArtistRole> roles = Set.of(SongArtistRole.WORDS_BY, SongArtistRole.MUSIC_BY);

        SongArtistInput input = new SongArtistInput(1L, data, roles);

        assertThat(input.id()).isEqualTo(1L);
        assertThat(input.data()).isSameAs(data);
        assertThat(input.roles()).isSameAs(roles);
        assertThat((IdAndDataContainer<ArtistData>) input).extracting(IdAndDataContainer::data)
            .isSameAs(data);
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        ArtistData data = new ArtistData("Alice", ArtistType.PERSON);
        SongArtistInput input = new SongArtistInput(1L, data, Set.of(SongArtistRole.WORDS_BY));
        SongArtistInput sameValues = new SongArtistInput(1L, data, Set.of(SongArtistRole.WORDS_BY));
        SongArtistInput differentRoles = new SongArtistInput(1L, data, Set.of(SongArtistRole.MUSIC_BY));

        assertThat(input)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentRoles);
        assertThat(input.toString()).contains("id=1", "roles=[WORDS_BY]");
    }
}
