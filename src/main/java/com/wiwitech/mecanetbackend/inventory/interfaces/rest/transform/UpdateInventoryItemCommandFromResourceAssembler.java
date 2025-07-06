package com.wiwitech.mecanetbackend.inventory.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.inventory.domain.model.commands.UpdateInventoryItemCommand;
import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.ItemCategory;
import com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources.UpdateInventoryItemResource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class UpdateInventoryItemCommandFromResourceAssembler {

    public UpdateInventoryItemCommand toCommand(Long itemId, UpdateInventoryItemResource resource) {
        // Validar y convertir la categoría
        ItemCategory category;
        try {
            category = ItemCategory.valueOf(resource.category());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoría inválida: " + resource.category() + 
                ". Valores válidos: " + Arrays.toString(ItemCategory.values()));
        }
        
        return new UpdateInventoryItemCommand(
            itemId,
            resource.name(),
            resource.description(),
            null, // brand
            null, // model
            category, // ✅ Usar la categoría convertida
            null, // subCategory
            null, // zone
            null, // building
            null, // area
            resource.location(), // locationCode
            resource.minimumStock(),
            null, // maximumStock
            resource.unit(), // unitOfMeasure - mapear el campo unit aquí
            resource.unitPrice(), // unitCost
            "USD", // currency por defecto
            null, // supplierName
            null, // supplierContact
            null, // supplierPhone
            null, // supplierEmail
            resource.compatibleMachineIds() != null ? 
                resource.compatibleMachineIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")) : 
                null // compatibleMachines como string
        );
    }
} 