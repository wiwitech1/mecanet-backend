package com.wiwitech.mecanetbackend.assetmanagment.domain.services;

import java.util.List;
import java.util.Optional;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.Machine;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetAllMachinesQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetAvailableMachinesQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetMachineByIdQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetMachineBySerialNumberQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetMachinesByProductionLineQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetMachinesDueForMaintenanceQuery;

/**
 * Machine query service interface
 * Handles machine query operations
 */
public interface MachineQueryService {
    
    /**
     * Handle get all machines query
     * @param query the get all machines query
     * @return list of machines
     */
    List<Machine> handle(GetAllMachinesQuery query);
    
    /**
     * Handle get machine by id query
     * @param query the get machine by id query
     * @return optional machine
     */
    Optional<Machine> handle(GetMachineByIdQuery query);
    
    /**
     * Handle get machine by serial number query
     * @param query the get machine by serial number query
     * @return optional machine
     */
    Optional<Machine> handle(GetMachineBySerialNumberQuery query);
    
    /**
     * Handle get machines by production line query
     * @param query the get machines by production line query
     * @return list of machines
     */
    List<Machine> handle(GetMachinesByProductionLineQuery query);
    
    /**
     * Handle get machines due for maintenance query
     * @param query the get machines due for maintenance query
     * @return list of machines due for maintenance
     */
    List<Machine> handle(GetMachinesDueForMaintenanceQuery query);
    
    /**
     * Handle get available machines query
     * @param query the get available machines query
     * @return list of available machines
     */
    List<Machine> handle(GetAvailableMachinesQuery query);
}