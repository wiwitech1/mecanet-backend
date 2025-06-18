package com.wiwitech.mecanetbackend.assetmanagment.domain.services;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.Plant;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.ActivatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.CreatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.DeactivatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.UpdatePlantCommand;

/**
 * Plant command service interface
 * Handles plant command operations
 */
public interface PlantCommandService {
    
    /**
     * Handle create plant command
     * @param command the create plant command
     * @return the created plant
     */
    Plant handle(CreatePlantCommand command);
    
    /**
     * Handle update plant command
     * @param command the update plant command
     * @return the updated plant
     */
    Plant handle(UpdatePlantCommand command);
    
    /**
     * Handle activate plant command
     * @param command the activate plant command
     * @return the activated plant
     */
    Plant handle(ActivatePlantCommand command);
    
    /**
     * Handle deactivate plant command
     * @param command the deactivate plant command
     * @return the deactivated plant
     */
    Plant handle(DeactivatePlantCommand command);
}