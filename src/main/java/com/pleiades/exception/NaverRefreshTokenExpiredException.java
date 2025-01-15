package com.pleiades.exception;

public class NaverRefreshTokenExpiredException extends RuntimeException {
    public NaverRefreshTokenExpiredException(String message) {
        super(message);
    }
}
