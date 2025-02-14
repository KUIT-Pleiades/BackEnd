package com.pleiades.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus getStatusCode() {
        return errorCode.getStatus();
    }

    public String getErrorMessage() {
        return errorCode.getMessage();
    }
}
