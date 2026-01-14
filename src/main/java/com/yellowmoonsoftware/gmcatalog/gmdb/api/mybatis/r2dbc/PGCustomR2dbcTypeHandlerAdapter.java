package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.r2dbc;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.PGParamMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.PGResultMapper;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.typehandlers.base.PGFunctionalCustomTypeHandler;
import io.r2dbc.spi.Readable;
import io.r2dbc.spi.ReadableMetadata;
import io.r2dbc.spi.Statement;
import lombok.extern.slf4j.Slf4j;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.executor.parameter.ParameterHandlerContext;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.executor.type.R2dbcTypeHandlerAdapter;

@Slf4j
public class PGCustomR2dbcTypeHandlerAdapter<T, R> extends PGFunctionalCustomTypeHandler<T, R> implements R2dbcTypeHandlerAdapter<T> {
    public PGCustomR2dbcTypeHandlerAdapter(final String pgType, final PGParamMapper<T> paramMapper, final PGResultMapper<R, T> resultMapper, final Class<T> type, final Class<R> resultTypeClass) {
        super(pgType, paramMapper, resultMapper, type, resultTypeClass);
    }

    @Override
    public Class<T> adaptClazz() {
        return this.getType();
    }

    @Override
    public void setParameter(Statement statement, ParameterHandlerContext parameterHandlerContext, T parameter) {
        statement.bind(parameterHandlerContext.getIndex(), this.convertParameter(parameter));
    }

    @Override
    public T getResult(io.r2dbc.spi.Readable readable, ReadableMetadata readableMetadata, String columnName) {
        log.debug("[R2DBC] Mapping readable named column, PG type {} => {}", getPgType(), getType().getSimpleName());
        return this.convertResult(readable.get(columnName, getResultTypeClass()));
    }

    @Override
    public T getResult(Readable readable, ReadableMetadata readableMetadata, int columnIndex) {
        log.debug("[R2DBC] Mapping readable indexed column, PG type {} => {}", getPgType(), getType().getSimpleName());
        return this.convertResult(readable.get(columnIndex, getResultTypeClass()));
    }
}
