package com.gemora_server.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends GemoraException {

    public BusinessException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}