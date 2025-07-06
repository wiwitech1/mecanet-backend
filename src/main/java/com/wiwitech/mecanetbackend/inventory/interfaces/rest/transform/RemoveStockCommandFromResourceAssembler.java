package com.wiwitech.mecanetbackend.inventory.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.inventory.domain.model.commands.RemoveStockCommand;
import com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources.RemoveStockResource;
import org.springframework.stereotype.Component;

@Component
public class RemoveStockCommandFromResourceAssembler {

    public RemoveStockCommand toCommand(Long itemId, RemoveStockResource resource, Long userId) {
        return new RemoveStockCommand(
            itemId,
            resource.quantity(),
            resource.reason(),
            userId,
            resource.reference(),     // Nuevo campo
            resource.machineId()      // Nuevo campo
        );
    }
} 