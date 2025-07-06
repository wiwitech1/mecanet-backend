package com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects;

/**
 * Estado operativo del técnico dentro de WorkOrders.
 */
public enum TechnicianStatus {
    AVAILABLE,
    BUSY,
    OFF_DUTY,
    ON_LEAVE
}