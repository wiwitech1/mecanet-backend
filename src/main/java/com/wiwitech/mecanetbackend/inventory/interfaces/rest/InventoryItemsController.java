package com.wiwitech.mecanetbackend.inventory.interfaces.rest;

import com.wiwitech.mecanetbackend.inventory.application.internal.commandservices.InventoryCommandServiceImpl;
import com.wiwitech.mecanetbackend.inventory.application.internal.queryservices.InventoryQueryServiceImpl;
import com.wiwitech.mecanetbackend.inventory.domain.model.aggregates.InventoryItem;
import com.wiwitech.mecanetbackend.inventory.domain.model.commands.*;
import com.wiwitech.mecanetbackend.inventory.domain.model.queries.*;
import com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources.*;
import com.wiwitech.mecanetbackend.inventory.interfaces.rest.transform.*;
import com.wiwitech.mecanetbackend.shared.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventory-items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory Items", description = "Gestión de items de inventario")
public class InventoryItemsController {

    private final InventoryCommandServiceImpl inventoryCommandService;
    private final InventoryQueryServiceImpl inventoryQueryService;
    private final InventoryItemResourceFromEntityAssembler inventoryItemResourceFromEntityAssembler;
    private final CreateInventoryItemCommandFromResourceAssembler createInventoryItemCommandFromResourceAssembler;
    private final UpdateInventoryItemCommandFromResourceAssembler updateInventoryItemCommandFromResourceAssembler;
    private final AddStockCommandFromResourceAssembler addStockCommandFromResourceAssembler;
    private final RemoveStockCommandFromResourceAssembler removeStockCommandFromResourceAssembler;

    @PostMapping
    @Operation(summary = "Crear nuevo item de inventario", description = "Crea un nuevo item de inventario con la información proporcionada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Item creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "SKU ya existe")
    })
    public ResponseEntity<InventoryItemResource> createInventoryItem(
            @RequestBody CreateInventoryItemResource resource) {
        log.info("Creando nuevo item de inventario: {}", resource.sku());
        
        CreateInventoryItemCommand command = createInventoryItemCommandFromResourceAssembler.toCommand(resource);
        InventoryItem inventoryItem = inventoryCommandService.handle(command);
        
        InventoryItemResource response = inventoryItemResourceFromEntityAssembler.toResource(inventoryItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar items de inventario", description = "Obtiene la lista paginada de items de inventario del tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    public ResponseEntity<Page<InventoryItemResource>> getAllInventoryItems(
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {
        log.info("Obteniendo items de inventario - página: {}, tamaño: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        GetAllInventoryItemsQuery query = new GetAllInventoryItemsQuery(pageable);
        Page<InventoryItem> inventoryItems = inventoryQueryService.handle(query);
        
        Page<InventoryItemResource> response = inventoryItems.map(inventoryItemResourceFromEntityAssembler::toResource);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Obtener item por ID", description = "Obtiene un item de inventario específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item encontrado"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    public ResponseEntity<InventoryItemResource> getInventoryItemById(
            @Parameter(description = "ID del item") @PathVariable Long itemId) {
        log.info("Obteniendo item de inventario por ID: {}", itemId);
        
        GetInventoryItemByIdQuery query = new GetInventoryItemByIdQuery(itemId);
        Optional<InventoryItem> inventoryItem = inventoryQueryService.handle(query);
        
        return inventoryItem
                .map(item -> ResponseEntity.ok(inventoryItemResourceFromEntityAssembler.toResource(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Obtener item por SKU", description = "Obtiene un item de inventario específico por su SKU")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item encontrado"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    public ResponseEntity<InventoryItemResource> getInventoryItemBySku(
            @Parameter(description = "SKU del item") @PathVariable String sku) {
        log.info("Obteniendo item de inventario por SKU: {}", sku);
        
        GetInventoryItemBySkuQuery query = new GetInventoryItemBySkuQuery(sku);
        Optional<InventoryItem> inventoryItem = inventoryQueryService.handle(query);
        
        return inventoryItem
                .map(item -> ResponseEntity.ok(inventoryItemResourceFromEntityAssembler.toResource(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Items con stock bajo", description = "Obtiene items que están por debajo del stock mínimo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    public ResponseEntity<List<InventoryItemResource>> getLowStockItems() {
        log.info("Obteniendo items con stock bajo");
        
        GetLowStockItemsQuery query = new GetLowStockItemsQuery();
        List<InventoryItem> lowStockItems = inventoryQueryService.handle(query);
        
        List<InventoryItemResource> response = lowStockItems.stream()
                .map(inventoryItemResourceFromEntityAssembler::toResource)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/machine/{machineId}")
    @Operation(summary = "Items compatibles con máquina", description = "Obtiene items compatibles con una máquina específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    public ResponseEntity<List<InventoryItemResource>> getItemsByMachine(
            @Parameter(description = "ID de la máquina") @PathVariable Long machineId) {
        log.info("Obteniendo items compatibles con máquina: {}", machineId);
        
        GetInventoryItemsByMachineQuery query = new GetInventoryItemsByMachineQuery(machineId);
        List<InventoryItem> compatibleItems = inventoryQueryService.handle(query);
        
        List<InventoryItemResource> response = compatibleItems.stream()
                .map(inventoryItemResourceFromEntityAssembler::toResource)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "Actualizar item", description = "Actualiza la información de un item de inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<InventoryItemResource> updateInventoryItem(
            @Parameter(description = "ID del item") @PathVariable Long itemId,
            @RequestBody UpdateInventoryItemResource resource) {
        log.info("Actualizando item de inventario: {}", itemId);
        
        UpdateInventoryItemCommand command = updateInventoryItemCommandFromResourceAssembler.toCommand(itemId, resource);
        InventoryItem inventoryItem = inventoryCommandService.handle(command);
        
        InventoryItemResource response = inventoryItemResourceFromEntityAssembler.toResource(inventoryItem);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{itemId}/add-stock")
    @Operation(summary = "Agregar stock", description = "Agrega stock a un item de inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock agregado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado"),
        @ApiResponse(responseCode = "400", description = "Cantidad inválida")
    })
    public ResponseEntity<InventoryItemResource> addStock(
            @Parameter(description = "ID del item") @PathVariable Long itemId,
            @RequestBody AddStockResource resource) {
        log.info("Agregando stock al item: {} - cantidad: {}", itemId, resource.quantity());
        
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        
        AddStockCommand command = addStockCommandFromResourceAssembler.toCommand(itemId, resource, userId);
        InventoryItem updatedItem = inventoryCommandService.handle(command);
        
        InventoryItemResource response = inventoryItemResourceFromEntityAssembler.toResource(updatedItem);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{itemId}/remove-stock")
    @Operation(summary = "Remover stock", description = "Remueve stock de un item de inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock removido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado"),
        @ApiResponse(responseCode = "400", description = "Stock insuficiente o cantidad inválida")
    })
    public ResponseEntity<InventoryItemResource> removeStock(
            @Parameter(description = "ID del item") @PathVariable Long itemId,
            @RequestBody RemoveStockResource resource) {
        log.info("Removiendo stock del item: {} - cantidad: {}", itemId, resource.quantity());
        
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        
        RemoveStockCommand command = removeStockCommandFromResourceAssembler.toCommand(itemId, resource, userId);
        InventoryItem updatedItem = inventoryCommandService.handle(command);
        
        InventoryItemResource response = inventoryItemResourceFromEntityAssembler.toResource(updatedItem);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Desactivar item", description = "Desactiva un item de inventario (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item desactivado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    public ResponseEntity<InventoryItemResource> deactivateInventoryItem(
            @Parameter(description = "ID del item") @PathVariable Long itemId) {
        log.info("Desactivando item de inventario: {}", itemId);
        
        DeactivateInventoryItemCommand command = new DeactivateInventoryItemCommand(new com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.InventoryItemId(itemId));
        InventoryItem inventoryItem = inventoryCommandService.handle(command);
        
        InventoryItemResource response = inventoryItemResourceFromEntityAssembler.toResource(inventoryItem);
        return ResponseEntity.ok(response);
    }
} 