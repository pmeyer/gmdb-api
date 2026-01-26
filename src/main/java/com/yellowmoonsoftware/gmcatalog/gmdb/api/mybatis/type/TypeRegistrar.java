package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.type;

public interface TypeRegistrar {
    TypeRegistrar register(final String packageName);
    TypeRegistrar register(final Class<?> clazz);
}
