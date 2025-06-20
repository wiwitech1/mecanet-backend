package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities.StaticTask;
import com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources.StaticTaskResource;

import java.util.stream.Collectors;

public class StaticTaskResourceFromEntityAssembler {
    
    public static StaticTaskResource toResource(StaticTask task) {
        return new StaticTaskResource(
                task.getId(),
                task.getMachineId().getValue(),
                task.getName(),
                task.getDescription(),
                task.getRequiredSkills().stream()
                    .map(skillId -> skillId.getValue())
                    .collect(Collectors.toSet())
        );
    }
}