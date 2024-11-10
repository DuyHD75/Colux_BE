package com.dcode.product_service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {
    private final String message;
    @Getter
    private final Object data;

    public BusinessException(String message) {
        super(message);
        this.message = message;
        this.data = null;
    }

    public BusinessException(String message, Object data) {
        super(message);
        this.message = message;
        this.data = data;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
