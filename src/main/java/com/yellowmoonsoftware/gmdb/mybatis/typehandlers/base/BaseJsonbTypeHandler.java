package com.yellowmoonsoftware.gmdb.mybatis.typehandlers.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellowmoonsoftware.gmdb.mybatis.PGDataConversionException;

public abstract class BaseJsonbTypeHandler<T> extends PGCustomTypeHandler<T, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.findAndRegisterModules();
    }

    protected BaseJsonbTypeHandler(Class<T> type) {
        super("jsonb", type, String.class);
    }

    @Override
    public String mapParam(final T value) throws PGDataConversionException {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new PGDataConversionException("Unable to convert object to JSON representation.", e);
        }
    }

    @Override
    public T mapColumnValue(final String value) throws PGDataConversionException {
        try {
            return objectMapper.readValue(value, this.getType());
        } catch (JsonProcessingException e) {
            throw new PGDataConversionException("Unable to deserialize object from JSON string", e);
        }
    }
}
