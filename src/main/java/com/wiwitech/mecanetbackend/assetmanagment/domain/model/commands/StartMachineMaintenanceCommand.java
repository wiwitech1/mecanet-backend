package com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands;

/**
 * Command to start maintenance on a machine
 */
public record StartMachineMaintenanceCommand(Long machineId) {
    public StartMachineMaintenanceCommand {
        if (machineId == null) {
            throw new IllegalArgumentException("Machine ID cannot be null");
        }
    }
}