package com.freelance.agency.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message){
        super(message);
    }
}
