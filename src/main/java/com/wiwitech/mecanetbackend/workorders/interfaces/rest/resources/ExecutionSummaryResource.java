package com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources;

import java.time.LocalDateTime;

/**
 * Resource representing execution summary information.
 */
public class ExecutionSummaryResource {
    public LocalDateTime startedAt;
    public LocalDateTime completedAt;
    public Long durationMinutes;
    public String conclusions;
    public Integer commentsCount;
    public Integer photosCount;
    
    public ExecutionSummaryResource() {}
    
    public ExecutionSummaryResource(LocalDateTime startedAt, LocalDateTime completedAt, 
                                  Long durationMinutes, String conclusions, 
                                  Integer commentsCount, Integer photosCount) {
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.durationMinutes = durationMinutes;
        this.conclusions = conclusions;
        this.commentsCount = commentsCount;
        this.photosCount = photosCount;
    }
} 