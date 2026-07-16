package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.r2dbc;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.PGDataConversionException;
import io.r2dbc.postgresql.codec.Json;
import io.r2dbc.spi.Readable;
import io.r2dbc.spi.ReadableMetadata;
import io.r2dbc.spi.Statement;
import lombok.extern.slf4j.Slf4j;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.executor.parameter.ParameterHandlerContext;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.executor.type.R2dbcTypeHandlerAdapter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
public class BaseJsonbTypeHandlerAdapter<T> implements R2dbcTypeHandlerAdapter<T> {
    private static final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    private final Class<T> type;

    public BaseJsonbTypeHandlerAdapter(Class<T> type) {
        this.type = type;
    }

    @Override
    public Class<T> adaptClazz() {
        return this.type;
    }

    @Override
    public void setParameter(Statement statement, ParameterHandlerContext parameterHandlerContext, T parameter) {
        try {
            log.debug("[{}] Binding parameter {} => JSONB", this.getClass().getSimpleName() ,parameter.getClass().getSimpleName());
            final String jsonString = objectMapper.writeValueAsString(parameter);
            statement.bind(parameterHandlerContext.getIndex(), Json.of(jsonString));
        } catch (JacksonException e) {
            throw new PGDataConversionException("Unable to convert object to JSON representation.", e);
        }
    }

    @Override
    public T getResult(Readable readable, ReadableMetadata readableMetadata, String columnName) {
        log.debug("[{}] Mapping result set column {} => JSONB", this.getClass().getSimpleName(), columnName);
        return mapColumnValue(readable.get(columnName, Json.class));
    }

    @Override
    public T getResult(Readable readable, ReadableMetadata readableMetadata, int columnIndex) {
        log.debug("[{}] Mapping result set column {} => JSONB", this.getClass().getSimpleName(), columnIndex);
        return mapColumnValue(readable.get(columnIndex, Json.class));
    }

    public T mapColumnValue(final Json value) throws PGDataConversionException {
        if (value == null) {
            return null;
        }

        return value.mapInputStream(i -> {
            try {
                return objectMapper.readValue(i, type);
            } catch (JacksonException e) {
                throw new PGDataConversionException("Unable to deserialize object from JSON string", e);
            }
        });
    }


}
