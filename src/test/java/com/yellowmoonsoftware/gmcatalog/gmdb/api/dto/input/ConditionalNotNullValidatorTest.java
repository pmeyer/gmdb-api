package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionalNotNullValidatorTest {

    @Test
    void testValid() {
        final ArtistInput foo = new ArtistInput(null, new ArtistData("Foo Fighters", ArtistType.BAND));

        try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {

            final Set<ConstraintViolation<ArtistInput>> validate = validatorFactory.getValidator().validate(foo);
            assertThat(validate).isEmpty();
        }
    }

    @Test
    void testInvalid() {
        final ArtistInput foo = new ArtistInput(null, null);

        try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {

            final Set<ConstraintViolation<ArtistInput>> validate = validatorFactory.getValidator().validate(foo);
            assertThat(validate).extracting(ConstraintViolation::getMessage).containsExactly("ArtistInput must have an ID or data");
        }
    }

    @Test
    void testInvalidCascade() {
        final ArtistInput foo = new ArtistInput(null, new ArtistData(null, null));

        try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {

            final Set<ConstraintViolation<ArtistInput>> validate = validatorFactory.getValidator().validate(foo);

            assertThat(validate).extracting(ConstraintViolation::getMessage)
                    .allMatch(m -> m.contains("must not be null"));
        }
    }
}