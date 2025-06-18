package com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries;

/**
 * Query to get a machine by its ID
 */
public record GetMachineByIdQuery(Long machineId) {
    public GetMachineByIdQuery {
        if (machineId == null) {
            throw new IllegalArgumentException("Machine ID cannot be null");
        }
    }
}