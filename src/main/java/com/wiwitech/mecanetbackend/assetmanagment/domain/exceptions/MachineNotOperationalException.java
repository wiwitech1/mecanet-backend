package com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions;

public class MachineNotOperationalException extends RuntimeException {
    public MachineNotOperationalException(String message) {
        super(message);
    }
}
