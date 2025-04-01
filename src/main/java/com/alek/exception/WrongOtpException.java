package com.alek.exception;

public class WrongOtpException extends Throwable {
    public WrongOtpException(String message) {
        super(message);
    }
}
