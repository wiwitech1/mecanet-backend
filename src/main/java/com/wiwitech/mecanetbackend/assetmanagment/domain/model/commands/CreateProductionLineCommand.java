package com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.Capacity;

/**
 * Command to create a new production line
 */
public record CreateProductionLineCommand(
    String name,
    String code,
    Capacity capacity,
    Long plantId
) {
    public CreateProductionLineCommand {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Production line name cannot be null or empty");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Production line code cannot be null or empty");
        }
        if (capacity == null) {
            throw new IllegalArgumentException("Production line capacity cannot be null");
        }
        if (plantId == null) {
            throw new IllegalArgumentException("Plant ID cannot be null");
        }
    }
}