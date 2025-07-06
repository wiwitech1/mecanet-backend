package com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions;

public class PlantAlreadyInactiveException extends RuntimeException {
    public PlantAlreadyInactiveException(String message) {
        super(message);
    }
}
