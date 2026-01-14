package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis;

@FunctionalInterface
public interface PGResultMapper<T, R> {
    R mapColumnValue(T value) throws PGDataConversionException;
}
