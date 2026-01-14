package com.yellowmoonsoftware.gmdb.mybatis.typehandlers.base;

import com.yellowmoonsoftware.gmdb.mybatis.PGDataConversionException;
import com.yellowmoonsoftware.gmdb.mybatis.PGParamMapper;
import com.yellowmoonsoftware.gmdb.mybatis.PGResultMapper;

public abstract class PGFunctionalCustomTypeHandler<T, R> extends  PGCustomTypeHandler<T, R> {
    private final PGParamMapper<T> paramMapper;
    private final PGResultMapper<R, T> resultMapper;

    public PGFunctionalCustomTypeHandler(final String pgType, final PGParamMapper<T> paramMapper, final PGResultMapper<R, T> resultMapper, final Class<T> type, final Class<R> resultTypeClass) {
        super(pgType, type, resultTypeClass);
        this.paramMapper = paramMapper;
        this.resultMapper = resultMapper;
    }

    @Override
    public String mapParam(final T value) throws PGDataConversionException {
        return this.paramMapper.mapParam(value);
    }

    @Override
    public T mapColumnValue(final R value) throws PGDataConversionException {
        return this.resultMapper.mapColumnValue(value);
    }
}
