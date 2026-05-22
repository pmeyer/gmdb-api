package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.r2dbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.PGDataConversionException;
import io.r2dbc.postgresql.codec.Json;
import io.r2dbc.spi.Readable;
import io.r2dbc.spi.ReadableMetadata;
import io.r2dbc.spi.Statement;
import lombok.extern.slf4j.Slf4j;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.executor.parameter.ParameterHandlerContext;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.executor.type.R2dbcTypeHandlerAdapter;

import java.io.IOException;

@Slf4j
public class BaseJsonbTypeHandlerAdapter<T> implements R2dbcTypeHandlerAdapter<T> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

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
        } catch (JsonProcessingException e) {
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
            } catch (IOException e) {
                throw new PGDataConversionException("Unable to deserialize object from JSON string", e);
            }
        });
    }


}
