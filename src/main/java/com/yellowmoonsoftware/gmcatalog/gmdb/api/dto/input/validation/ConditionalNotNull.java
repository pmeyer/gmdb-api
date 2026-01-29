package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Controls validation of the field specified by {@link #value()}.  The nullability of fields
 * in {@link #ifNull()} control whether the field specified by {@link #value()} must be non-null.
 * The value of {@link #checkType()} controls whether ANY or ALL of the fields in {@link #ifNull()}
 * must be null to force the field specified by {@link #value()} to be non-null.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConditionalNotNullValidator.class)
@Repeatable(ConditionalNotNulls.class)
public @interface ConditionalNotNull {
    String message() default "Field \"{value}\" must be non-null if {checkType} of fields {ifNull} are null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Name of the field that must be non-null if ANY or ALL fields in {@link #ifNull()}
     * are null.  {@link #checkType()} controls ANY or ALL behavior.
     * @see #ifNull()
     * @see #checkType()
     * @return the name of the field
     */
    String value();

    /**
     * Names of fields that when ANY or ALL are null force the field specified by {@link #value()}
     * to be non-null.
     * @see #value()
     * @see #checkType()
     * @return an array of field names
     */
    String[] ifNull();

    /**
     * Whether ALL or ANY of the fields in {@link #ifNull()} must be null to force the field
     * specified by {@link #value()} to be non-null.
     * @see #value()
     * @see #ifNull()
     * @return the type of boolean check to perform
     */
    CheckType checkType() default CheckType.ANY;


    /**
     * Controls the validation behavior of the {@link NonNull} validation annotation.  Values are
     * ANY or ALL.
     * @see NonNull
     * @see NonNull#checkType()
     */
    public enum CheckType {
        /**
         * If ANY of the fields in {@link NonNull#ifNull()} are null, the field specified
         * by {@link NonNull#value()} must be non-null.
         */
        ANY,
        /**
         * If ALL of the fields in {@link NonNull#ifNull()} are null, the field specified
         * by {@link NonNull#value()} must be non-null.
         */
        ALL
    }
}

