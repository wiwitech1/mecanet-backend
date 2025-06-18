package com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries;

/**
 * Query to get a machine by its serial number
 */
public record GetMachineBySerialNumberQuery(String serialNumber) {
    public GetMachineBySerialNumberQuery {
        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Serial number cannot be null or empty");
        }
    }
}