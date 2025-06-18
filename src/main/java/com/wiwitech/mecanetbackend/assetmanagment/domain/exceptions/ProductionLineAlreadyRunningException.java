package com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions;

public class ProductionLineAlreadyRunningException extends RuntimeException {
    public ProductionLineAlreadyRunningException(String message) {
        super(message);
    }
}
