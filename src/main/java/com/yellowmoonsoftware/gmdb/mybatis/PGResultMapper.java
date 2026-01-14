package com.yellowmoonsoftware.gmdb.mybatis;

@FunctionalInterface
public interface PGResultMapper<T, R> {
    R mapColumnValue(T value) throws PGDataConversionException;
}
