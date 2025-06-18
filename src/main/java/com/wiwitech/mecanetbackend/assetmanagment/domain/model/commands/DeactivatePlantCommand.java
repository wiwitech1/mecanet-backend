package com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands;

/**
 * Command to deactivate a plant
 */
public record DeactivatePlantCommand(Long plantId) {
    public DeactivatePlantCommand {
        if (plantId == null) {
            throw new IllegalArgumentException("Plant ID cannot be null");
        }
    }
}