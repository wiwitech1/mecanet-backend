package com.wiwitech.mecanetbackend.workorders.domain.exceptions;

public class InvalidWorkOrderStateException extends RuntimeException {
    public InvalidWorkOrderStateException(String message) {super(message);} 
} 