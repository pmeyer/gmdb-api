package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumInputTest {

    @Test
    void exposesRecordValuesAndDataContract() {
        final AlbumData data = albumData();

        final AlbumInput input = new AlbumInput(1L, data);

        assertThat(input.id()).isEqualTo(1L);
        assertThat(input.data()).isSameAs(data);
        assertThat((IdAndDataContainer<AlbumData>) input).extracting(IdAndDataContainer::data)
            .isSameAs(data);
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        final AlbumData data = albumData();
        final AlbumInput input = new AlbumInput(1L, data);
        final AlbumInput sameValues = new AlbumInput(1L, data);
        final AlbumInput differentId = new AlbumInput(2L, data);

        assertThat(input)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentId);
        assertThat(input.toString()).contains("id=1");
    }

    private static AlbumData albumData() {
        final ArtistInput artist = new ArtistInput(10L, new ArtistData("Alice", ArtistType.PERSON));
        return new AlbumData("Live Set", null, LocalDate.of(2020, 4, 5), artist);
    }
}
