package com.yellowmoonsoftware.gmdb.mappers.typehandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public abstract class PGCustomTypeHandler<T, R> extends BaseTypeHandler<T> {

    private static final PGobject customType = new PGobject();

    private final String pgType;
    private final Function<T, String> paramMapper;
    private final Class<R> resultTypeClass;
    private final Function<R, T> resultMapper;

    protected PGCustomTypeHandler(final String pgType, final Class<R> resultTypeClass, final Function<R, T> resultMapper) {
        this(pgType, Object::toString, resultTypeClass, resultMapper);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        log.info("Setting parameter {} => custom PG type {}", parameter.getClass().getSimpleName(), this.pgType);
        customType.setValue(paramMapper.apply(parameter));
        customType.setType(this.pgType);
        ps.setObject(i, customType);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        log.info("Mapping result set named column, custom PG type {}", this.pgType);
        return processColumnValue(rs.getObject(columnName, resultTypeClass));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        log.info("Mapping result set indexed column, custom PG type {}", this.pgType);
        return processColumnValue(rs.getObject(columnIndex, resultTypeClass));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        log.info("Mapping callable statement result, custom PG type {}", this.pgType);
        return processColumnValue(cs.getObject(columnIndex, resultTypeClass));
    }

    private T processColumnValue(final R colValue) throws SQLDataException {
        if (colValue == null) {
            return null;
        }
        try {
            return resultMapper.apply(colValue);
        } catch (Exception e) {
            throw new SQLDataException("The value %s could not be converted to a target value".formatted(colValue), e);
        }
    }
}
