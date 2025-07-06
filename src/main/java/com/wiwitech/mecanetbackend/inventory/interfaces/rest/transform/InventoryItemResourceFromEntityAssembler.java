package com.wiwitech.mecanetbackend.inventory.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.inventory.domain.model.aggregates.InventoryItem;
import com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources.InventoryItemResource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InventoryItemResourceFromEntityAssembler {

    public InventoryItemResource toResource(InventoryItem inventoryItem) {
        // Parsear compatibleMachines string a Set<Long>
        Set<Long> compatibleMachineIds = null;
        if (inventoryItem.getCompatibleMachines() != null && !inventoryItem.getCompatibleMachines().isEmpty()) {
            compatibleMachineIds = Arrays.stream(inventoryItem.getCompatibleMachines().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toSet());
        }
        
        // Convertir Date a LocalDateTime
        LocalDateTime createdAt = convertToLocalDateTime(inventoryItem.getCreatedAt());
        LocalDateTime updatedAt = convertToLocalDateTime(inventoryItem.getUpdatedAt());
        
        return new InventoryItemResource(
            inventoryItem.getId(),
            inventoryItem.getSku(),
            inventoryItem.getName(),
            inventoryItem.getDescription(),
            inventoryItem.getCategory().name(),
            inventoryItem.getUnitOfMeasure(),
            inventoryItem.getUnitCost(),
            inventoryItem.getCurrentStock(),
            inventoryItem.getMinimumStock(),
            inventoryItem.getLocationCode(),
            compatibleMachineIds,
            inventoryItem.getStatus().name(),
            createdAt,
            updatedAt
        );
    }
    
    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
} 