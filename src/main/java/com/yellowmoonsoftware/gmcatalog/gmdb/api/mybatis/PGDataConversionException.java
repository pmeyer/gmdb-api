package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis;

import java.sql.SQLDataException;
import java.sql.SQLException;

public class PGDataConversionException extends RuntimeException {
    public PGDataConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SQLException asSqlException() {
        if (getCause() instanceof SQLException sqlException) {
            return sqlException;
        }

        return new SQLDataException(getMessage(), this.getCause());
    }
}
