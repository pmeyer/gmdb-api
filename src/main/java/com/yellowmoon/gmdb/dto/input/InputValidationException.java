package com.yellowmoon.gmdb.dto.input;

public class InputValidationException extends RuntimeException {
    public InputValidationException(String message) {
        super(message);
    }
}
