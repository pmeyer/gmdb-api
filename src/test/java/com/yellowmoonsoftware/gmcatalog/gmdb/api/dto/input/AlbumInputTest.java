package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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

    @Test
    void validatesWhenIdOrDataIsPresent() {
        final AlbumInput idOnly = new AlbumInput(1L, null);
        final AlbumInput dataOnly = new AlbumInput(null, albumData());

        assertThat(ValidationTestSupport.validate(idOnly)).isEmpty();
        assertThat(ValidationTestSupport.validate(dataOnly)).isEmpty();
    }

    @Test
    void validatesIdOrDataRequirement() {
        final AlbumInput input = new AlbumInput(null, null);

        assertThat(ValidationTestSupport.validate(input))
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("AlbumInput must have an ID or data");
    }

    @Test
    void cascadesValidationToAlbumData() {
        final ArtistInput invalidArtist = new ArtistInput(null, null);
        final AlbumData data = new AlbumData("Live Set", null, LocalDate.of(2020, 4, 5), invalidArtist);
        final AlbumInput input = new AlbumInput(null, data);

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactly(tuple("data.primaryArtist", "ArtistInput must have an ID or data"));
    }

    private static AlbumData albumData() {
        final ArtistInput artist = new ArtistInput(10L, new ArtistData("Alice", ArtistType.PERSON));
        return new AlbumData("Live Set", null, LocalDate.of(2020, 4, 5), artist);
    }
}
