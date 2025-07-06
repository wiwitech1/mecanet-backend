package com.wiwitech.mecanetbackend.workorders.domain.exceptions;

/**
 * Thrown when attempting to add a technician and the WorkOrder already reached its capacity.
 */
public class TechnicianLimitExceededException extends RuntimeException {
    public TechnicianLimitExceededException(String message) {
        super(message);
    }
}