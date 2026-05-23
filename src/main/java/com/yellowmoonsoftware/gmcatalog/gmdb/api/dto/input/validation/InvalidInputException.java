package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
