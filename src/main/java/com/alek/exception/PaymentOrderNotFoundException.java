package com.alek.exception;

public class PaymentOrderNotFoundException extends Exception {
    public PaymentOrderNotFoundException(String message) {
        super(message);
    }
}
