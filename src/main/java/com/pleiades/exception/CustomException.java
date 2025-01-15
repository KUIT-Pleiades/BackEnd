package com.pleiades.exception;

public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return errorCode.getStatus();
    }

    public String getErrorMessage() {
        return errorCode.getMessage();
    }
}
