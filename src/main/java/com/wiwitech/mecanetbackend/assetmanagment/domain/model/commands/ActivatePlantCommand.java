package com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands;

/**
 * Command to activate a plant
 */
public record ActivatePlantCommand(Long plantId) {
    public ActivatePlantCommand {
        if (plantId == null) {
            throw new IllegalArgumentException("Plant ID cannot be null");
        }
    }
}