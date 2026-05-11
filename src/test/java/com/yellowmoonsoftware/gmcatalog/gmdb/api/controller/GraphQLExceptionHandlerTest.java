package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InputValidationException;
import graphql.ErrorType;
import graphql.GraphQLError;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GraphQLExceptionHandlerTest {

    private final GraphQLExceptionHandler handler = new GraphQLExceptionHandler();

    @Test
    void handleConstraintViolationExceptionReturnsValidationError() {
        ConstraintViolationException exception = new ConstraintViolationException("constraint failed", null);

        GraphQLError error = handler.handleConstraintViolationException(exception);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.ValidationError);
        assertThat(error.getMessage()).isEqualTo("constraint failed");
    }

    @Test
    void handlerInputValidationExceptionReturnsValidationError() {
        InputValidationException exception = new InputValidationException("input failed");

        GraphQLError error = handler.handlerInputValidationException(exception);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.ValidationError);
        assertThat(error.getMessage()).isEqualTo("input failed");
    }
}
