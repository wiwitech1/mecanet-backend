package com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands;

/**
 * Command to assign a machine to a production line
 */
public record AssignMachineToProductionLineCommand(
    Long machineId,
    Long productionLineId
) {
    public AssignMachineToProductionLineCommand {
        if (machineId == null) {
            throw new IllegalArgumentException("Machine ID cannot be null");
        }
        if (productionLineId == null) {
            throw new IllegalArgumentException("Production line ID cannot be null");
        }
    }
}