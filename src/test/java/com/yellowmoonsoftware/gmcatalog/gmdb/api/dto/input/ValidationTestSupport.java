package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.lang.reflect.Constructor;
import java.util.Set;

final class ValidationTestSupport {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private ValidationTestSupport() {
    }

    static <T> Set<ConstraintViolation<T>> validate(final T value) {
        return VALIDATOR.validate(value);
    }

    static <T> Set<ConstraintViolation<T>> validateConstructorParameters(final Constructor<T> constructor, final Object... parameters) {
        return VALIDATOR.forExecutables().validateConstructorParameters(constructor, parameters);
    }
}
