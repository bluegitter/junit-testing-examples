package com.company.training.service;

public class RemoteServiceUnavailableException extends RuntimeException {

    public RemoteServiceUnavailableException(String message) {
        super(message);
    }
}
