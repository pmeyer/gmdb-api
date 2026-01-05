package com.yellowmoonsoftware.gmdb.dto.input;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConditionalNotNullValidator.class)
public @interface ConditionalNotNull {

    String message() default "Field must not be null if {field} is null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field();
}


