package com.wiwitech.mecanetbackend.workorders.domain.model.queries;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianId;
 
/**
 * Query to get a technician by ID.
 */
public record GetTechnicianByIdQuery(TechnicianId technicianId) {} 