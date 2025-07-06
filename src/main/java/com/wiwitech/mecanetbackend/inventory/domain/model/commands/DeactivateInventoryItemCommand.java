package com.wiwitech.mecanetbackend.inventory.domain.model.commands;

import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.InventoryItemId;

public record DeactivateInventoryItemCommand(InventoryItemId itemId) {} 