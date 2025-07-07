package com.wiwitech.mecanetbackend.workorders.domain.model.queries;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianStatus;
 
/**
 * Query to get technicians by status.
 */
public record GetTechniciansByStatusQuery(TechnicianStatus status) {} 