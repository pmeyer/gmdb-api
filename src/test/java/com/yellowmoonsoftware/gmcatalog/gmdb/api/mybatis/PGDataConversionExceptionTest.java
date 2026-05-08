package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis;

import org.junit.jupiter.api.Test;

import java.sql.SQLDataException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class PGDataConversionExceptionTest {

    @Test
    void storesMessageAndCause() {
        final Throwable cause = new IllegalArgumentException("bad data");
        final PGDataConversionException exception = new PGDataConversionException("conversion failed", cause);

        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("conversion failed")
                .hasCause(cause);
    }

    @Test
    void asSqlExceptionReturnsExistingSqlCause() {
        final SQLException cause = new SQLException("sql failed");
        final PGDataConversionException exception = new PGDataConversionException("conversion failed", cause);
        final SQLException sqlException = exception.asSqlException();

        assertThat((Object) sqlException).isSameAs(cause);
    }

    @Test
    void asSqlExceptionWrapsNonSqlCause() {
        final Throwable cause = new IllegalArgumentException("bad data");
        final PGDataConversionException exception = new PGDataConversionException("conversion failed", cause);
        final SQLException sqlException = exception.asSqlException();

        assertThat((Object) sqlException).isInstanceOf(SQLDataException.class);
        assertThat(sqlException.getMessage()).isEqualTo("conversion failed");
        assertThat(sqlException.getCause()).isSameAs(cause);
    }
}
