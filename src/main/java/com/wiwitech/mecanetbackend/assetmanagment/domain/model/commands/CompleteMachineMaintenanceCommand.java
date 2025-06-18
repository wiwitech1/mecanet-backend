package com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands;

/**
 * Command to complete maintenance on a machine
 */
public record CompleteMachineMaintenanceCommand(Long machineId) {
    public CompleteMachineMaintenanceCommand {
        if (machineId == null) {
            throw new IllegalArgumentException("Machine ID cannot be null");
        }
    }
}