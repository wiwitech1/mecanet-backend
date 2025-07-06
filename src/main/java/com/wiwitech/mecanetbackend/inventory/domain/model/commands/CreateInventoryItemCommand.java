package com.wiwitech.mecanetbackend.inventory.domain.model.commands;

import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.ItemCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Command para crear un nuevo item de inventario
 */
public record CreateInventoryItemCommand(
    String sku,
    String name,
    String description,
    String brand,
    String model,
    String partNumber,
    ItemCategory category,
    String subCategory,
    Long plantId,
    String zone,
    String building,
    String area,
    String locationCode,
    Integer initialStock,
    Integer minimumStock,
    Integer maximumStock,
    String unitOfMeasure,
    BigDecimal unitCost,
    String currency,
    String supplierName,
    String supplierContact,
    String supplierPhone,
    String supplierEmail,
    String supplierCode,
    LocalDate expirationDate,
    String compatibleMachines
) {} 