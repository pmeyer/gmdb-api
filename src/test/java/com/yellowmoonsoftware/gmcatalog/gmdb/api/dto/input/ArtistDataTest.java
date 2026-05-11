package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ArtistDataTest {

    @Test
    void exposesRecordValues() {
        final ArtistData data = new ArtistData("Alice", ArtistType.PERSON);

        assertThat(data.name()).isEqualTo("Alice");
        assertThat(data.type()).isEqualTo(ArtistType.PERSON);
    }

    @Test
    void validatesRequiredFields() {
        final ArtistData data = new ArtistData(null, null);

        assertThat(ValidationTestSupport.validate(data))
            .extracting(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)
            .containsExactlyInAnyOrder(
                tuple("name", "must not be null"),
                tuple("type", "must not be null")
            );
    }
}
