package com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions;

public class ProductionLineNotReadyException extends RuntimeException {
    public ProductionLineNotReadyException(String message) {
        super(message);
    }
}
