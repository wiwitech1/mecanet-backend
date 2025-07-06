package com.wiwitech.mecanetbackend.inventory.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.inventory.domain.model.entities.StockMovement;
import com.wiwitech.mecanetbackend.inventory.domain.model.events.InventoryItemCreatedEvent;
import com.wiwitech.mecanetbackend.inventory.domain.model.events.StockAddedEvent;
import com.wiwitech.mecanetbackend.inventory.domain.model.events.StockRemovedEvent;
import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.StockOperationType;
import com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manejadores de eventos de dominio para el BC Inventory
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventHandlers {

    private final StockMovementRepository stockMovementRepository;

    @EventListener
    @Transactional
    public void handleInventoryItemCreatedEvent(InventoryItemCreatedEvent event) {
        log.info("Procesando evento de creación de item: {}", event.sku());
        // Solo logear por ahora, el stock inicial se maneja en el create command
    }

    @EventListener
    @Transactional
    public void handleStockAddedEvent(StockAddedEvent event) {
        log.info("Procesando evento de adición de stock: {} - cantidad: {}", event.sku(), event.quantity());
        
        // Crear registro de movimiento de stock
        StockMovement stockMovement = new StockMovement(
            event.itemId(),
            event.sku(),
            StockOperationType.IN,  // Usar IN para entrada de stock
            event.quantity(),
            event.previousStock(),
            event.newStock(),
            event.reason(),
            event.reference(),
            "Stock agregado: " + event.reason(),
            event.userId(),
            "Usuario", // TODO: Obtener nombre real del usuario
            event.plantId(),
            "Almacén", // TODO: Obtener ubicación real
            event.tenantId(),
            event.unitCost(),
            null  // machineId no aplica para entradas
        );
        
        stockMovementRepository.save(stockMovement);
        log.info("Registro de movimiento de stock creado: IN {} unidades para item {}", 
                event.quantity(), event.sku());
    }

    @EventListener
    @Transactional
    public void handleStockRemovedEvent(StockRemovedEvent event) {
        log.info("Procesando evento de remoción de stock: {} - cantidad: {}", event.sku(), event.quantity());
        
        // Crear registro de movimiento de stock
        StockMovement stockMovement = new StockMovement(
            event.itemId(),
            event.sku(),
            StockOperationType.OUT,  // Usar OUT para salida de stock
            event.quantity(),
            event.previousStock(),
            event.newStock(),
            event.reason(),
            event.reference(),
            "Stock removido: " + event.reason(),
            event.userId(),
            "Usuario", // TODO: Obtener nombre real del usuario
            event.plantId(),
            "Almacén", // TODO: Obtener ubicación real
            event.tenantId(),
            null, // unitCost no aplica para salidas
            event.machineId()
        );
        
        stockMovementRepository.save(stockMovement);
        log.info("Registro de movimiento de stock creado: OUT {} unidades para item {}", 
                event.quantity(), event.sku());
    }
} 