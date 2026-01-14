package com.yellowmoonsoftware.gmdb.mybatis.typehandlers.base;

import com.yellowmoonsoftware.gmdb.mybatis.PGDataConversionException;
import com.yellowmoonsoftware.gmdb.mybatis.PGParamMapper;
import com.yellowmoonsoftware.gmdb.mybatis.PGResultMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.*;

@Slf4j
@Getter
@RequiredArgsConstructor
public abstract class PGCustomTypeHandler<T, R> extends BaseTypeHandler<T> implements PGParamMapper<T>, PGResultMapper<R,T> {

    private static final PGobject customType = new PGobject();

    private final String pgType;
    private final Class<T> type;
    private final Class<R> resultTypeClass;

    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int i, final T parameter, final JdbcType jdbcType) throws SQLException {
        try {
            log.info("Setting parameter {} => custom PG type {}", parameter.getClass().getSimpleName(), this.getPgType());
            ps.setObject(i, this.convertParameter(parameter));
        } catch (PGDataConversionException e) {
            throw e.asSqlException();
        }
    }

    @Override
    public T getNullableResult(final ResultSet rs, final String columnName) throws SQLException {
        try {
            log.debug("Mapping result set named column {}", columnName);
            return convertResult(rs.getObject(columnName, resultTypeClass));
        } catch (PGDataConversionException e) {
            throw e.asSqlException();
        }
    }

    @Override
    public T getNullableResult(final ResultSet rs, final int columnIndex) throws SQLException {
        try {
            log.debug("Mapping result set indexed column {}", columnIndex);
            return convertResult(rs.getObject(columnIndex, resultTypeClass));
        } catch (PGDataConversionException e) {
            throw e.asSqlException();
        }
    }

    @Override
    public T getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        try {
            log.debug("Mapping callable statement result, column index {}", columnIndex);
            return convertResult(cs.getObject(columnIndex, resultTypeClass));
        } catch (PGDataConversionException e) {
            throw e.asSqlException();
        }
    }

    public PGobject convertParameter(final T parameter) throws PGDataConversionException {
        try {
            log.debug("Setting parameter {} => custom PG type {}", parameter.getClass().getSimpleName(), pgType);
            customType.setValue(this.mapParam(parameter));
            return customType;
        } catch(PGDataConversionException e) {
            throw e;
        } catch (Exception e) {
            throw new PGDataConversionException("Error setting value on PGobject.", e);
        }
    }

    public T convertResult(final R colValue) throws PGDataConversionException {
        if (colValue == null) {
            return null;
        }

        try {
            log.debug("Mapping custom PG type {} => {}", this.pgType, this.type.getSimpleName());
            return this.mapColumnValue(colValue);
        } catch(PGDataConversionException e) {
            throw e;
        } catch (Exception e) {
            throw new PGDataConversionException("The value %s [%s] could not be converted to a target value".formatted(colValue,
                    colValue.getClass().getSimpleName()), e);
        }
    }
}

