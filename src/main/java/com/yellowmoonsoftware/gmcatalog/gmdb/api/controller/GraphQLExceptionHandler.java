package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InputValidationException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
import graphql.ErrorType;
import graphql.GraphQLError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GraphQLExceptionHandler {

    @GraphQlExceptionHandler
    public GraphQLError handleConstraintViolationException(final ConstraintViolationException e) {
        return GraphQLError.newError()
                .errorType(ErrorType.ValidationError)
                .message(e.getMessage())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handlerInputValidationException(final InputValidationException e) {
        return GraphQLError.newError()
                .errorType(ErrorType.ValidationError)
                .message(e.getMessage())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handlerInvalidInputException(final InvalidInputException e) {
        return GraphQLError.newError()
                .errorType(ErrorType.ValidationError)
                .message(e.getMessage())
                .build();
    }
}
