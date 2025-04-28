package com.personal.RealTimeNotification.exception;

import org.springframework.http.HttpStatus;

public class CustomAuthException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final HttpStatus status;

    public CustomAuthException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

