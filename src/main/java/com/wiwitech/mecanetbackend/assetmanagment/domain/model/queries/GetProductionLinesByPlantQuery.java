package com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries;

/**
 * Query to get all production lines for a specific plant
 */
public record GetProductionLinesByPlantQuery(Long plantId) {
    public GetProductionLinesByPlantQuery {
        if (plantId == null) {
            throw new IllegalArgumentException("Plant ID cannot be null");
        }
    }
}