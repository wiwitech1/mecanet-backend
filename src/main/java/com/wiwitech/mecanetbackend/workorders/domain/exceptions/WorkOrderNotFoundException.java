package com.wiwitech.mecanetbackend.workorders.domain.exceptions;

/**
 * Thrown when the requested WorkOrder cannot be located.
 */
public class WorkOrderNotFoundException extends RuntimeException {
    public WorkOrderNotFoundException(String message) {
        super(message);
    }
} 