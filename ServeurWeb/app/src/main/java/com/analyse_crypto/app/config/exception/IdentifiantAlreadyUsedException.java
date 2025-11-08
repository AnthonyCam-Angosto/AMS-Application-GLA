package com.analyse_crypto.app.config.exception;

public class IdentifiantAlreadyUsedException extends RuntimeException {
    public IdentifiantAlreadyUsedException(String message) {
        super(message);
    }   
}
