package com.yellowmoon.gmdb.dto.input;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConditionalNotNullValidatorTest {

    @Test
    void testValidation() {

        final GqlInputTypes.ArtistInput foo = new GqlInputTypes.ArtistInput(null, "Foo", null);

        try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {

            Set<ConstraintViolation<GqlInputTypes.ArtistInput>> validate = validatorFactory.getValidator().validate(foo);
            System.out.println(validate);
        }
    }

}