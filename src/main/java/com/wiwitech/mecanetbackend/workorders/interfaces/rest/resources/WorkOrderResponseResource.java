package com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources;

import java.util.Set;
import java.util.List;
import java.time.LocalDateTime;

public class WorkOrderResponseResource {
    public Long id;
    public Long planId;
    public Long taskId;
    public Long machineId;
    public String title;
    public String description;
    public String status;
    public Integer maxTechnicians;
    public Set<Long> requiredSkillIds;
    public String schedule;
    public String executionWindow;
    public String conclusions;
    public List<String> comments;
    public List<String> photos;
    public Long tenantId;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
} 