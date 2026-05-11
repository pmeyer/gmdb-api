package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ArtistInputTest {

    @Test
    void exposesRecordValuesAndDataContract() {
        final ArtistData data = new ArtistData("Alice", ArtistType.PERSON);

        final ArtistInput input = new ArtistInput(1L, data);

        assertThat(input.id()).isEqualTo(1L);
        assertThat(input.data()).isSameAs(data);
        assertThat((IdAndDataContainer<ArtistData>) input).extracting(IdAndDataContainer::data)
            .isSameAs(data);
    }

    @Test
    void supportsRecordEqualityAndStringRepresentation() {
        final ArtistData data = new ArtistData("Alice", ArtistType.PERSON);
        final ArtistInput input = new ArtistInput(1L, data);
        final ArtistInput sameValues = new ArtistInput(1L, data);
        final ArtistInput differentId = new ArtistInput(2L, data);

        assertThat(input)
            .isEqualTo(sameValues)
            .hasSameHashCodeAs(sameValues)
            .isNotEqualTo(differentId);
        assertThat(input.toString()).contains("id=1", "Alice");
    }

    @Test
    void validatesWhenIdOrDataIsPresent() {
        final ArtistInput idOnly = new ArtistInput(1L, null);
        final ArtistInput dataOnly = new ArtistInput(null, new ArtistData("Alice", ArtistType.PERSON));

        assertThat(ValidationTestSupport.validate(idOnly)).isEmpty();
        assertThat(ValidationTestSupport.validate(dataOnly)).isEmpty();
    }

    @Test
    void validatesIdOrDataRequirement() {
        final ArtistInput input = new ArtistInput(null, null);

        assertThat(ValidationTestSupport.validate(input))
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("ArtistInput must have an ID or data");
    }

    @Test
    void cascadesValidationToArtistData() {
        final ArtistInput input = new ArtistInput(null, new ArtistData(null, null));

        assertThat(ValidationTestSupport.validate(input))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactlyInAnyOrder(
                tuple("data.name", "must not be null"),
                tuple("data.type", "must not be null")
            );
    }
}
