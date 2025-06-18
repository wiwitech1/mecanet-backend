package com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries;

/**
 * Query to get a production line by its ID
 */
public record GetProductionLineByIdQuery(Long productionLineId) {
    public GetProductionLineByIdQuery {
        if (productionLineId == null) {
            throw new IllegalArgumentException("Production line ID cannot be null");
        }
    }
}