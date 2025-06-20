package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities.DynamicTask;
import com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources.DynamicTaskResource;

import java.util.stream.Collectors;

public class DynamicTaskResourceFromEntityAssembler {
    
    public static DynamicTaskResource toResource(DynamicTask task) {
        return new DynamicTaskResource(
                task.getId(),
                task.getMachineId().getValue(),
                task.getName(),
                task.getDescription(),
                task.getRequiredSkills().stream()
                    .map(skillId -> skillId.getValue())
                    .collect(Collectors.toSet()),
                task.getStatus(),
                task.getTriggeredAt()
        );
    }
}