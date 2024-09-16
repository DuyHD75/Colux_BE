package com.dcode.customer_service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerExceptions extends RuntimeException {

    public CustomerExceptions() {
    super("An error occurred while processing your request. Please try again.");
    }

    public CustomerExceptions(String message) {
        super(message);
    }

}
