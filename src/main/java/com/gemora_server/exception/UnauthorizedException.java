package com.gemora_server.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends GemoraException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}