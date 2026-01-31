package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalNotNulls {
    ConditionalNotNull[] value();
}
