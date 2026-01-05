package com.yellowmoonsoftware.gmdb.dto.input;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class ConditionalNotNullValidator implements ConstraintValidator<ConditionalNotNull, Object> {

    private String field;
    private Set<String> fieldGetters;

    @Override
    public void initialize(ConditionalNotNull constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldGetters = Set.of(field, "get" + field.substring(0, 1).toUpperCase() + field.substring(1));
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {

           return getFieldAccessor(value.getClass())
                    .map(m -> {
                        try {
                            final Object fieldValue = m.invoke(value);
                            if (fieldValue == null) {
                                return false;
                            }
                        } catch (Exception e) {
                            // Handle exceptions appropriately
                        }
                        return true;
                    }).orElse(false);


    }

    private Optional<Method> getFieldAccessor(final Class<?> clazz) {

        return Arrays.stream(clazz.getMethods()).filter(m -> fieldGetters.contains(m.getName())).findFirst();
    }
}
