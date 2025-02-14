package com.pleiades.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_USERNAME_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid username or password"),
    INVALID_USER_EMAIL(HttpStatus.UNAUTHORIZED, "Invalid user email"),
    INVALID_USER_ID(HttpStatus.UNAUTHORIZED, "Invalid user id"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid or expired token"),
    MISSING_AUTH_HEADER(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header"),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "Access denied"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    STATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Station not found"),
    USER_ALREADY_IN_STATION(HttpStatus.CONFLICT, "User already in the station");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
