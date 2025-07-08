package com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources;

import java.time.LocalDateTime;

/**
 * Resource representing technician summary information in work orders.
 */
public class TechnicianSummaryResource {
    public Long technicianId;
    public String fullName;
    public String role;
    public String status;
    public LocalDateTime joinedAt;
    public String withdrawalReason;
    
    public TechnicianSummaryResource() {}
    
    public TechnicianSummaryResource(Long technicianId, String fullName, String role, String status, 
                                   LocalDateTime joinedAt, String withdrawalReason) {
        this.technicianId = technicianId;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.joinedAt = joinedAt;
        this.withdrawalReason = withdrawalReason;
    }
} 