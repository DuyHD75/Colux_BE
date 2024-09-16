package com.dcode.customer_service.handler;

import com.dcode.customer_service.exception.CustomerExceptions;
import com.dcode.customer_service.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(CustomerExceptions.class)
    public ResponseEntity<String> handle(CustomerExceptions exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException exception) {

        var error = new HashMap<String, String>();

        exception.getBindingResult().getAllErrors().forEach((e) -> {
            var fieldName = e.getObjectName();
            var errorMessage = e.getDefaultMessage();
            error.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(error));
    }

}
