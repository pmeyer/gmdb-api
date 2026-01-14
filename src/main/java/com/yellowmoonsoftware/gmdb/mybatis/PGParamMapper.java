package com.yellowmoonsoftware.gmdb.mybatis;

@FunctionalInterface
public interface PGParamMapper<T> {
    String mapParam(T value) throws PGDataConversionException;
}
