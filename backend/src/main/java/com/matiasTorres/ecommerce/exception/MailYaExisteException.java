package com.matiasTorres.ecommerce.exception;

public class MailYaExisteException extends RuntimeException {
    public MailYaExisteException(String message) {
        super(message);
    }
}
