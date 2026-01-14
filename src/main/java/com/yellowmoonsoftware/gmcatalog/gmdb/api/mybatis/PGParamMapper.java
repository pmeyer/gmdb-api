package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis;

@FunctionalInterface
public interface PGParamMapper<T> {
    String mapParam(T value) throws PGDataConversionException;
}
