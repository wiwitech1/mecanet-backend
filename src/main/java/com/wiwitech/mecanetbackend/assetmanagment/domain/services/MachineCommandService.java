package com.wiwitech.mecanetbackend.assetmanagment.domain.services;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.Machine;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.AssignMachineToProductionLineCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.CompleteMachineMaintenanceCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.RegisterMachineCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.StartMachineMaintenanceCommand;

/**
 * Machine command service interface
 * Handles machine command operations
 */
public interface MachineCommandService {
    
    /**
     * Handle register machine command
     * @param command the register machine command
     * @return the registered machine
     */
    Machine handle(RegisterMachineCommand command);
    
    /**
     * Handle assign machine to production line command
     * @param command the assign machine command
     * @return the assigned machine
     */
    Machine handle(AssignMachineToProductionLineCommand command);
    
    /**
     * Handle start machine maintenance command
     * @param command the start maintenance command
     * @return the machine
     */
    Machine handle(StartMachineMaintenanceCommand command);
    
    /**
     * Handle complete machine maintenance command
     * @param command the complete maintenance command
     * @return the machine
     */
    Machine handle(CompleteMachineMaintenanceCommand command);
}