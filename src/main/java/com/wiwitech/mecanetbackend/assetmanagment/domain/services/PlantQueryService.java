package com.wiwitech.mecanetbackend.assetmanagment.domain.services;

import java.util.List;
import java.util.Optional;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.Plant;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetActivePlantsQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetAllPlantsQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetPlantByIdQuery;

/**
 * Plant query service interface
 * Handles plant query operations
 */
public interface PlantQueryService {
    
    /**
     * Handle get all plants query
     * @param query the get all plants query
     * @return list of plants
     */
    List<Plant> handle(GetAllPlantsQuery query);
    
    /**
     * Handle get plant by id query
     * @param query the get plant by id query
     * @return optional plant
     */
    Optional<Plant> handle(GetPlantByIdQuery query);
    
    /**
     * Handle get active plants query
     * @param query the get active plants query
     * @return list of active plants
     */
    List<Plant> handle(GetActivePlantsQuery query);
}