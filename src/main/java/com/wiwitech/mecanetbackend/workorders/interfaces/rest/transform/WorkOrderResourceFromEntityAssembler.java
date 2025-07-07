package com.wiwitech.mecanetbackend.workorders.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources.WorkOrderResponseResource;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.SkillId;
import java.util.stream.Collectors;
import java.time.ZoneId;

public class WorkOrderResourceFromEntityAssembler {
    public static WorkOrderResponseResource toResource(WorkOrder wo) {
        WorkOrderResponseResource dto = new WorkOrderResponseResource();
        dto.id = wo.getWorkOrderId() != null ? wo.getWorkOrderId().getValue() : null;
        dto.planId = wo.getPlanId() != null ? wo.getPlanId().getValue() : null;
        dto.taskId = wo.getTaskId() != null ? wo.getTaskId().getValue() : null;
        dto.machineId = wo.getMachineId() != null ? wo.getMachineId().getValue() : null;
        dto.title = wo.getTitle();
        dto.description = wo.getDescription();
        dto.status = wo.getStatus() != null ? wo.getStatus().name() : null;
        dto.maxTechnicians = wo.getMaxTechnicians();
        dto.requiredSkillIds = wo.getRequiredSkillIds() != null ? wo.getRequiredSkillIds().stream().map(SkillId::value).collect(Collectors.toSet()) : null;
        dto.schedule = wo.getSchedule() != null ? wo.getSchedule().toString() : null;
        dto.executionWindow = wo.getExecutionWindow() != null ? wo.getExecutionWindow().toString() : null;
        dto.conclusions = wo.getConclusions();
        dto.comments = wo.getComments() != null ? wo.getComments().stream().map(c -> c.getText()).collect(Collectors.toList()) : null;
        dto.photos = wo.getPhotos() != null ? wo.getPhotos().stream().map(p -> p.getUrl()).collect(Collectors.toList()) : null;
        dto.tenantId = wo.getTenantId() != null ? wo.getTenantId().getValue() : null;
        dto.createdAt = wo.getCreatedAt() != null ? wo.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
        dto.updatedAt = wo.getUpdatedAt() != null ? wo.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
        return dto;
    }
} 