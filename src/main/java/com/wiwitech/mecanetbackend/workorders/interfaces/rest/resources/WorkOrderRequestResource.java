package com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class WorkOrderRequestResource {
    @NotNull
    public Long planId;
    @NotNull
    public Long taskId;
    @NotNull
    public Long machineId;
    @NotNull
    @Size(min = 3, max = 100)
    public String title;
    public String description;
    @NotNull
    public Set<Long> requiredSkillIds;
    @NotNull
    public Long tenantId;
} 