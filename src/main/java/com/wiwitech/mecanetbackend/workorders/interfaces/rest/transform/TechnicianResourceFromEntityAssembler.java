package com.wiwitech.mecanetbackend.workorders.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources.TechnicianResponseResource;
import java.util.stream.Collectors;

public class TechnicianResourceFromEntityAssembler {
    public static TechnicianResponseResource toResource(Technician tech) {
        TechnicianResponseResource dto = new TechnicianResponseResource();
        dto.id = tech.getId();
        dto.iamUserId = tech.getIamUserId() != null ? tech.getIamUserId().getValue() : null;
        dto.username = tech.getUsername();
        dto.firstName = tech.getFirstName();
        dto.lastName = tech.getLastName();
        dto.email = tech.getEmail() != null ? tech.getEmail().value() : null;
        dto.phoneNumber = tech.getPhoneNumber() != null ? tech.getPhoneNumber().value() : null;
        dto.status = tech.getCurrentStatus() != null ? tech.getCurrentStatus().name() : null;
        dto.shift = tech.getShift() != null ? tech.getShift().name() : null;
        dto.skills = tech.getSkills() != null ? tech.getSkills().stream().map(s -> s.value()).collect(Collectors.toSet()) : null;
        dto.supervisorId = tech.getSupervisor() != null ? tech.getSupervisor().getId() : null;
        dto.tenantId = tech.getTenantId() != null ? tech.getTenantId().getValue() : null;
        return dto;
    }
} 