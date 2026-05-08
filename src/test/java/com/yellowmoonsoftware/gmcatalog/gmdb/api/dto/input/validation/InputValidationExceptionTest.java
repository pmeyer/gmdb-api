package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputValidationExceptionTest {

    @Test
    void storesValidationMessage() {
        final InputValidationException exception = new InputValidationException("invalid input");

        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("invalid input");
    }
}
