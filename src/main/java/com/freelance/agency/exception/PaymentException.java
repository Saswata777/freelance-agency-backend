package com.freelance.agency.exception;

public class PaymentException extends RuntimeException{
    public PaymentException(String message){
        super(message);
    }
}
