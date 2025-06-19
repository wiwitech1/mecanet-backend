package com.wiwitech.mecanetbackend.workorders.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.iam.domain.model.events.UserCreatedEvent;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import com.wiwitech.mecanetbackend.workorders.domain.model.commands.CreateTechnicianCommand;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.UserId;
import com.wiwitech.mecanetbackend.workorders.domain.services.TechnicianCommandService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedEventHandler {

    private static final String TECHNICIAN_ROLE = "ROLE_TECHNICAL";
    private final TechnicianCommandService service;

    public UserCreatedEventHandler(TechnicianCommandService service) {
        this.service = service;
    }

    @EventListener
    public void on(UserCreatedEvent e) {
        if (!e.roles().contains(TECHNICIAN_ROLE)) return;
        try {
            TenantContext.setCurrentTenantId(e.tenantId());
            service.handle(new CreateTechnicianCommand(
                new UserId(e.userId()),
                e.username(),
                e.firstName(),
                e.lastName(),
                new EmailAddress(e.email())
            ));
        } finally {
            TenantContext.clear();
        }
    }
}