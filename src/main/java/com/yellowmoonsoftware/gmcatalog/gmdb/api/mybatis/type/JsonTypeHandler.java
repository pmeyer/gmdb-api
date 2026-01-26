package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.type;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JsonTypeHandler {
    boolean mapDescendants() default false;
}
