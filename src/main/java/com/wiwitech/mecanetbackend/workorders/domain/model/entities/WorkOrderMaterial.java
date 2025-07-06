package com.wiwitech.mecanetbackend.workorders.domain.model.entities;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.InventoryItemId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "work_order_materials")
@NoArgsConstructor
public class WorkOrderMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private InventoryItemId itemId;

    @Column(nullable = false, length = 50)
    private String itemSku;

    @Column(nullable = false, length = 100)
    private String itemName;

    @Column(nullable = false)
    private Integer requestedQty;

    private Integer finalQty;

    public WorkOrderMaterial(InventoryItemId itemId, String sku, String name, Integer requestedQty) {
        if (requestedQty == null || requestedQty <= 0)
            throw new IllegalArgumentException("requestedQty must be positive");
        this.itemId = itemId;
        this.itemSku = sku;
        this.itemName = name;
        this.requestedQty = requestedQty;
    }

    public void updateRequestedQty(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("qty must be positive");
        this.requestedQty = qty;
    }

    public void setFinalQty(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("qty must be positive");
        this.finalQty = qty;
    }
} 