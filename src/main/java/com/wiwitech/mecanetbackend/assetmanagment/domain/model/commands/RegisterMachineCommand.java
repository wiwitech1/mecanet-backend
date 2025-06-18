package com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.MachineSpecs;

/**
 * Command to register a new machine
 */
public record RegisterMachineCommand(
    String serialNumber,
    String name,
    MachineSpecs specs
) {
    public RegisterMachineCommand {
        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Machine serial number cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Machine name cannot be null or empty");
        }
        if (specs == null) {
            throw new IllegalArgumentException("Machine specs cannot be null");
        }
    }
}