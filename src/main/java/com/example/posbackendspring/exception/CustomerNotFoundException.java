package com.example.posbackendspring.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException() {}
    public CustomerNotFoundException(String message) {}
    public CustomerNotFoundException(String message, Throwable cause) {}

}