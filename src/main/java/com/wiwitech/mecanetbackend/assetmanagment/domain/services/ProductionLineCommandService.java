package com.wiwitech.mecanetbackend.assetmanagment.domain.services;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.ProductionLine;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.CreateProductionLineCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.StartProductionCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.StopProductionCommand;

/**
 * Production line command service interface
 * Handles production line command operations
 */
public interface ProductionLineCommandService {
    
    /**
     * Handle create production line command
     * @param command the create production line command
     * @return the created production line
     */
    ProductionLine handle(CreateProductionLineCommand command);
    
    /**
     * Handle start production command
     * @param command the start production command
     * @return the production line
     */
    ProductionLine handle(StartProductionCommand command);
    
    /**
     * Handle stop production command
     * @param command the stop production command
     * @return the production line
     */
    ProductionLine handle(StopProductionCommand command);
}