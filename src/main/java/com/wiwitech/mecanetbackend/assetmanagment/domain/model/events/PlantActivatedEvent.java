package com.wiwitech.mecanetbackend.assetmanagment.domain.model.events;

import java.time.LocalDateTime;

public record PlantActivatedEvent(
    Long plantId,
    String plantName,
    LocalDateTime occurredOn
) {
    public PlantActivatedEvent(Long plantId, String plantName) {
        this(plantId, plantName, LocalDateTime.now());
    }
}