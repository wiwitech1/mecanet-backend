package com.wiwitech.mecanetbackend.workorders.domain.model.events;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.UserId;

public record TechnicianCreatedEvent(
        Long technicianId,
        UserId iamUserId,
        Long tenantId
) {}