package com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.ContactInfo;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.Location;

/**
 * Command to update plant information
 */
public record UpdatePlantCommand(
    Long plantId,
    String name,
    Location location,
    ContactInfo contactInfo
) {
    public UpdatePlantCommand {
        if (plantId == null) {
            throw new IllegalArgumentException("Plant ID cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Plant name cannot be null or empty");
        }
        if (location == null) {
            throw new IllegalArgumentException("Plant location cannot be null");
        }
        if (contactInfo == null) {
            throw new IllegalArgumentException("Plant contact info cannot be null");
        }
    }
}