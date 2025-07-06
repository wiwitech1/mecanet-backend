package com.wiwitech.mecanetbackend.inventory.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.inventory.domain.model.entities.StockMovement;
import com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources.StockMovementResource;
import com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories.InventoryItemRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class StockMovementResourceFromEntityAssembler {

    private final InventoryItemRepository inventoryItemRepository;

    public StockMovementResourceFromEntityAssembler(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public StockMovementResource toResource(StockMovement stockMovement) {
        // Convertir Date a LocalDateTime
        LocalDateTime createdAt = convertToLocalDateTime(stockMovement.getCreatedAt());
        
        // Obtener el nombre del item desde el repositorio
        String itemName = inventoryItemRepository.findById(stockMovement.getInventoryItemId())
            .map(item -> item.getName())
            .orElse("Item no encontrado");
        
        return new StockMovementResource(
            stockMovement.getId(),
            stockMovement.getInventoryItemId(), // ✅ Método correcto
            stockMovement.getSku(), // ✅ Método correcto
            itemName, // ✅ Nombre obtenido del repositorio
            stockMovement.getOperationType().name(), // ✅ Método correcto
            stockMovement.getQuantity(),
            stockMovement.getPreviousStock(),
            stockMovement.getNewStock(),
            stockMovement.getReason(),
            stockMovement.getUserId(),
            stockMovement.getUserName(),
            createdAt,
            stockMovement.getUnitCost(),
            stockMovement.getTotalCost()
        );
    }
    
    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
} 