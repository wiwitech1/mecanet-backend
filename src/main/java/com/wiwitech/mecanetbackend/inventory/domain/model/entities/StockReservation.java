package com.wiwitech.mecanetbackend.inventory.domain.model.entities;

import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.ReservationStatus;
import com.wiwitech.mecanetbackend.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que registra las reservas de stock
 */
@Entity
@Table(name = "stock_reservations",
    indexes = {
        @Index(name = "idx_inventory_item_id", columnList = "inventory_item_id"),
        @Index(name = "idx_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_reservation_date", columnList = "reservation_date"),
        @Index(name = "idx_expiration_date", columnList = "expiration_date"),
        @Index(name = "idx_sku", columnList = "sku")
    }
)
@Getter
@NoArgsConstructor
public class StockReservation extends AuditableModel {

    @Column(name = "inventory_item_id", nullable = false)
    private Long inventoryItemId;

    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "reservation_type", length = 50)
    private String reservationType;

    @Column(name = "reference", length = 100)
    private String reference;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "reservation_date", nullable = false)
    private LocalDateTime reservationDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    public StockReservation(
            Long inventoryItemId,
            String sku,
            Integer reservedQuantity,
            Integer availableQuantity,
            String reservationType,
            String reference,
            String description,
            Long userId,
            String userName,
            LocalDateTime expirationDate,
            Long tenantId
    ) {
        this.inventoryItemId = inventoryItemId;
        this.sku = sku;
        this.reservedQuantity = reservedQuantity;
        this.availableQuantity = availableQuantity;
        this.status = ReservationStatus.ACTIVE;
        this.reservationType = reservationType;
        this.reference = reference;
        this.description = description;
        this.userId = userId;
        this.userName = userName;
        this.reservationDate = LocalDateTime.now();
        this.expirationDate = expirationDate;
        this.tenantId = tenantId;
    }

    public void release() {
        this.status = ReservationStatus.RELEASED;
        this.releaseDate = LocalDateTime.now();
    }

    public void expire() {
        this.status = ReservationStatus.EXPIRED;
    }
} 