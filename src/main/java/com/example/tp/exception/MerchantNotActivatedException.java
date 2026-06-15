package com.example.tp.exception;

public class MerchantNotActivatedException extends RuntimeException {
    public MerchantNotActivatedException(String message) {
        super(message);
    }
}
