package com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries;

/**
 * Query to get all machines assigned to a production line
 */
public record GetMachinesByProductionLineQuery(Long productionLineId) {
    public GetMachinesByProductionLineQuery {
        if (productionLineId == null) {
            throw new IllegalArgumentException("Production line ID cannot be null");
        }
    }
}