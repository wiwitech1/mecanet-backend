package com.wiwitech.mecanetbackend.workorders.domain.services;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import com.wiwitech.mecanetbackend.workorders.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Domain service that processes Technician-related queries.
 */
public interface TechnicianQueryService {
    Optional<Technician> handle(GetTechnicianByIdQuery query);
    Optional<Technician> handle(GetTechnicianByIamUserIdQuery query);
    List<Technician> handle(GetTechniciansByStatusQuery query);
    List<Technician> handle(GetTechniciansBySkillQuery query);
} 