package com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands;

/**
 * Command to stop production on a production line
 */
public record StopProductionCommand(
    Long productionLineId,
    String reason
) {
    public StopProductionCommand {
        if (productionLineId == null) {
            throw new IllegalArgumentException("Production line ID cannot be null");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Stop reason cannot be null or empty");
        }
    }
    
    // Constructor sin reason (usa razón por defecto)
    public StopProductionCommand(Long productionLineId) {
        this(productionLineId, "Manual stop");
    }
}