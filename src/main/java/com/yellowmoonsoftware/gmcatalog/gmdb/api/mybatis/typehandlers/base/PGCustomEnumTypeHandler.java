package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers.base;

public abstract class PGCustomEnumTypeHandler<T extends Enum<T>> extends PGFunctionalCustomTypeHandler<T, String> {
    public PGCustomEnumTypeHandler(final String pgType, final Class<T> enumType) {
        super(pgType, Enum::name, s -> Enum.valueOf(enumType, s), enumType, String.class);
    }
}
