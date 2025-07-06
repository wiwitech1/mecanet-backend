package com.wiwitech.mecanetbackend.inventory.domain.model.commands;

import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.ItemCategory;

import java.math.BigDecimal;

/**
 * Command para actualizar un item de inventario existente
 */
public record UpdateInventoryItemCommand(
    Long itemId,
    String name,
    String description,
    String brand,
    String model,
    ItemCategory category,
    String subCategory,
    String zone,
    String building,
    String area,
    String locationCode,
    Integer minimumStock,
    Integer maximumStock,
    String unitOfMeasure,
    BigDecimal unitCost,
    String currency,
    String supplierName,
    String supplierContact,
    String supplierPhone,
    String supplierEmail,
    String compatibleMachines
) {} 