package com.wiwitech.mecanetbackend.inventory.domain.model.queries;

import org.springframework.data.domain.Pageable;

public record GetAllInventoryItemsQuery(Pageable pageable) {} 