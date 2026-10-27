package com.matiasTorres.ecommerce.exception;

public class PagoException extends RuntimeException {
    public PagoException(String message, Throwable causa) {
        super(message, causa);
    }
}
