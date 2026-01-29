package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ConditionalNotNullValidator implements ConstraintValidator<ConditionalNotNull, Object> {

    private ConditionalNotNull conditionalNotNull;

    @Override
    public void initialize(ConditionalNotNull constraintAnnotation) {
        conditionalNotNull = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        final ConditionalNotNull.CheckType checkType = conditionalNotNull.checkType();
        return !checkFieldNull(value, conditionalNotNull.checkType(), conditionalNotNull.ifNull()) ||
                getField(value, conditionalNotNull.value()) != null;
    }

    protected boolean checkFieldNull(final Object val, final ConditionalNotNull.CheckType checkType, final String... fieldNames) {
        return checkType == ConditionalNotNull.CheckType.ANY
                ? Arrays.stream(fieldNames).anyMatch(f -> getField(val, f) == null)
                : Arrays.stream(fieldNames).allMatch(f -> getField(val, f) == null);
    }


    protected Object getField(final Object val, final String fieldName) {
        try {
            final Field f = val.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(val);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
