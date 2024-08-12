package com.dcode.microservices.api_gateway.exception;

public class ApiException extends RuntimeException {

    public ApiException() {
        super("An error occurred while processing your request. Please try again.");
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
