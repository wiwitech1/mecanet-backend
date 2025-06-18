package com.wiwitech.mecanetbackend.assetmanagment.domain.services;

import java.util.List;
import java.util.Optional;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.ProductionLine;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetProductionLineByIdQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetProductionLinesByPlantQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetRunningProductionLinesQuery;

/**
 * Production line query service interface
 * Handles production line query operations
 */
public interface ProductionLineQueryService {
    
    /**
     * Handle get production lines by plant query
     * @param query the get production lines by plant query
     * @return list of production lines
     */
    List<ProductionLine> handle(GetProductionLinesByPlantQuery query);
    
    /**
     * Handle get production line by id query
     * @param query the get production line by id query
     * @return optional production line
     */
    Optional<ProductionLine> handle(GetProductionLineByIdQuery query);
    
    /**
     * Handle get running production lines query
     * @param query the get running production lines query
     * @return list of running production lines
     */
    List<ProductionLine> handle(GetRunningProductionLinesQuery query);
}