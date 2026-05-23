package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InputValidationException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
import graphql.ErrorType;
import graphql.GraphQLError;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GraphQLExceptionHandlerTest {

    private final GraphQLExceptionHandler handler = new GraphQLExceptionHandler();

    @Test
    void handleConstraintViolationExceptionReturnsValidationError() {
        final ConstraintViolationException exception = new ConstraintViolationException("constraint failed", null);

        final GraphQLError error = handler.handleConstraintViolationException(exception);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.ValidationError);
        assertThat(error.getMessage()).isEqualTo("constraint failed");
    }

    @Test
    void handlerInputValidationExceptionReturnsValidationError() {
        final InputValidationException exception = new InputValidationException("input failed");

        final GraphQLError error = handler.handlerInputValidationException(exception);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.ValidationError);
        assertThat(error.getMessage()).isEqualTo("input failed");
    }

    @Test
    void handlerInvalidInputExceptionReturnsValidationError() {
        final InvalidInputException exception = new InvalidInputException("invalid input");

        final GraphQLError error = handler.handlerInvalidInputException(exception);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.ValidationError);
        assertThat(error.getMessage()).isEqualTo("invalid input");
    }
}
