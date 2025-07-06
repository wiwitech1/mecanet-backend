package com.wiwitech.mecanetbackend.inventory.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.inventory.domain.model.commands.CreateInventoryItemCommand;
import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.ItemCategory;
import com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources.CreateInventoryItemResource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.Arrays;

@Component
public class CreateInventoryItemCommandFromResourceAssembler {

    public CreateInventoryItemCommand toCommand(CreateInventoryItemResource resource) {
        try {
            ItemCategory category = ItemCategory.valueOf(resource.category());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoría inválida: " + resource.category() + 
                ". Valores válidos: " + Arrays.toString(ItemCategory.values()));
        }
        return new CreateInventoryItemCommand(
            resource.sku(),
            resource.name(),
            resource.description(),
            null, // brand
            null, // model
            null, // partNumber
            ItemCategory.valueOf(resource.category()),
            null, // subCategory
            resource.plantId(), // plantId del resource
            null, // zone
            null, // building
            null, // area
            resource.location(), // locationCode
            0, // initialStock
            resource.minimumStock(),
            null, // maximumStock
            resource.unit(), // unitOfMeasure
            resource.unitPrice(), // unitCost
            "USD", // currency por defecto
            null, // supplierName
            null, // supplierContact
            null, // supplierPhone
            null, // supplierEmail
            null, // supplierCode
            null, // expirationDate
            resource.compatibleMachineIds() != null ? 
                resource.compatibleMachineIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")) : 
                null // compatibleMachines como string
        );
    }
} 