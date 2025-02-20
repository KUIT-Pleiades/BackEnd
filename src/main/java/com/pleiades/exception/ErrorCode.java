package com.pleiades.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_USERNAME_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid username or password"),
    INVALID_USER_EMAIL(HttpStatus.UNAUTHORIZED, "Invalid user email"),
    INVALID_USER_ID(HttpStatus.UNAUTHORIZED, "Invalid user id"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid or expired token"),
    INVALID_STATION_ID(HttpStatus.BAD_REQUEST, "Invalid station id"),
    MISSING_AUTH_HEADER(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header"),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "Access denied"),
    FORBIDDEN_MEMBER(HttpStatus.FORBIDDEN, "You are not a member of this station."),
    USER_NOT_IN_STATION(HttpStatus.NOT_FOUND, "Target User is not in a station"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    STAR_NOT_FOUND(HttpStatus.NOT_FOUND, "Star not found"),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "Friend request not found"),
    SIGNAL_NOT_FOUND(HttpStatus.NOT_FOUND, "Signal not found"),
    STATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Station not found"),
    USER_ALREADY_IN_STATION(HttpStatus.CONFLICT, "User already in the station"),
    REPORT_REQUIRED(HttpStatus.ACCEPTED, "You must submit a report before entering this station."),
    STATION_FULL(HttpStatus.CONFLICT, "Station Full. You cannot enter the station."),
    USER_NEVER_ENTERED_STATION(HttpStatus.NOT_ACCEPTABLE, "User never entered the station."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Image not found"),
    INFORMATION_NOT_VALID(HttpStatus.FORBIDDEN, "Invalid information"),
    ALREADY_RECEIVED_FRIEND_REQUEST(HttpStatus.CONFLICT, "You already received a friend request"),
    ALREADY_SENT_SIGNAL(HttpStatus.CONFLICT, "You already sent a signal"),
    SIGN_UP_REQUIRED(HttpStatus.ACCEPTED, "Need Sign-up");

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
