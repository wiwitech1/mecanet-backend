package com.wiwitech.mecanetbackend.workorders.domain.services;

import com.wiwitech.mecanetbackend.workorders.domain.model.commands.CreateTechnicianCommand;
import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;

public interface TechnicianCommandService {
    Technician handle(CreateTechnicianCommand command);
}