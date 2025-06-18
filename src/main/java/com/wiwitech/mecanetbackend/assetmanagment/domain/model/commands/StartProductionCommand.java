package com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands;

/**
 * Command to start production on a production line
 */
public record StartProductionCommand(Long productionLineId) {
    public StartProductionCommand {
        if (productionLineId == null) {
            throw new IllegalArgumentException("Production line ID cannot be null");
        }
    }
}