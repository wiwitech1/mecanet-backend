package com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions;

public class ProductionLineAlreadyStoppedException extends RuntimeException {
    public ProductionLineAlreadyStoppedException(String message) {
        super(message);
    }
}
