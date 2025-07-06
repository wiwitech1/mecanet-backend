package com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries;

/**
 * Query to get a plant by its ID
 */
public record GetPlantByIdQuery(Long plantId) {
    public GetPlantByIdQuery {
        if (plantId == null) {
            throw new IllegalArgumentException("Plant ID cannot be null");
        }
    }
}