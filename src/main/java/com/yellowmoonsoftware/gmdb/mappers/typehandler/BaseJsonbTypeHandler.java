package com.yellowmoonsoftware.gmdb.mappers.typehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.*;

@Slf4j
public abstract class BaseJsonbTypeHandler<T> extends BaseTypeHandler<T> {
    private static final PGobject jsonObject = new PGobject();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.findAndRegisterModules();
    }

    private final Class<T> type;

    public BaseJsonbTypeHandler(@NonNull final Class<T> type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        log.info("Setting parameter {} => jsonb PG type", type);
        try {
            jsonObject.setValue(objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new SQLDataException("Unable to convert object to JSON representation.", e);
        }

        jsonObject.setType("jsonb");
        ps.setObject(i, jsonObject);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        log.info("Mapping result set indexed column, PG type jsonb => {}}", type);
        return processColumnValue(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        log.info("Mapping result set named column, PG type jsonb => {}}", type);
        return processColumnValue(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        log.info("Mapping callable statement result, PG type jsonb => {}}", type);
        return processColumnValue(cs.getString(columnIndex));
    }

    private T processColumnValue(final String colValue) throws SQLDataException {
        if (colValue == null) {
            return null;
        }
        try {
            return objectMapper.readValue(colValue, type);
        } catch (JsonProcessingException e) {
            throw new SQLDataException("Unable to deserialize object from JSON string", e);
        }
    }
}
