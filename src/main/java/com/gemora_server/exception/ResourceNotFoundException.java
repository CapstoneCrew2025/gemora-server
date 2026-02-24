package com.gemora_server.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends GemoraException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}