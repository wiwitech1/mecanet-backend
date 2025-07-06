package com.wiwitech.mecanetbackend.inventory.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.inventory.domain.model.commands.AddStockCommand;
import com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources.AddStockResource;
import org.springframework.stereotype.Component;

@Component
public class AddStockCommandFromResourceAssembler {

    public AddStockCommand toCommand(Long itemId, AddStockResource resource, Long userId) {
        return new AddStockCommand(
            itemId,
            resource.quantity(),
            resource.reason(),
            userId,
            resource.unitCost(),      // Nuevo campo
            resource.reference()      // Nuevo campo
        );
    }
} 