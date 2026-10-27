package com.matiasTorres.ecommerce.exception;

public class StripeNoConfiguradoException extends RuntimeException {
    public StripeNoConfiguradoException(String message) {
        super(message);
    }
}
