package com.wiwitech.mecanetbackend.subscription.domain.exceptions;

public class SubscriptionLimitExceededException extends RuntimeException {
    
    public SubscriptionLimitExceededException(String resourceType, Integer limit) {
        super(String.format("Límite de %s excedido. Límite actual: %d", resourceType, limit));
    }
    
    public SubscriptionLimitExceededException(String message) {
        super(message);
    }
    
    public SubscriptionLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
} 