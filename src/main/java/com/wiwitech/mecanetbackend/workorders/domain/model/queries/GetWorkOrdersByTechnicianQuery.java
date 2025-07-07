package com.wiwitech.mecanetbackend.workorders.domain.model.queries;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianId;
 
/**
 * Query to get work orders by technician.
 */
public record GetWorkOrdersByTechnicianQuery(TechnicianId technicianId) {} 