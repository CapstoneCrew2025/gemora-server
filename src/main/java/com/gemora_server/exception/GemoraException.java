package com.gemora_server.exception;

import org.springframework.http.HttpStatus;

public class GemoraException extends RuntimeException {

    private final HttpStatus status;

    public GemoraException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
