package com.wiwitech.mecanetbackend.workorders.domain.model.queries;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.MachineId;
 
/**
 * Query to get work orders by machine.
 */
public record GetWorkOrdersByMachineQuery(MachineId machineId) {} 