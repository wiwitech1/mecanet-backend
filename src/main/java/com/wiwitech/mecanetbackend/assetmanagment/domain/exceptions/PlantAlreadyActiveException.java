package com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions;

public class PlantAlreadyActiveException extends RuntimeException {
    public PlantAlreadyActiveException(String message) {
        super(message);
    }
}
