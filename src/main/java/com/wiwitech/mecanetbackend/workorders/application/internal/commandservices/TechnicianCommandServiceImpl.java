package com.wiwitech.mecanetbackend.workorders.application.internal.commandservices;

import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import com.wiwitech.mecanetbackend.workorders.domain.model.commands.CreateTechnicianCommand;
import com.wiwitech.mecanetbackend.workorders.domain.model.events.TechnicianCreatedEvent;
import com.wiwitech.mecanetbackend.workorders.domain.services.TechnicianCommandService;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.TechnicianRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TechnicianCommandServiceImpl implements TechnicianCommandService {

    private static final Logger logger = LoggerFactory.getLogger(TechnicianCommandServiceImpl.class);

    private final TechnicianRepository technicianRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TechnicianCommandServiceImpl(TechnicianRepository technicianRepository,
                                        ApplicationEventPublisher eventPublisher) {
        this.technicianRepository = technicianRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Technician handle(CreateTechnicianCommand command) {
        logger.info("Creating technician for IAM user: {}", command.iamUserId().getValue());

        Long tenantIdValue = TenantContext.getCurrentTenantId();
        if (tenantIdValue == null) {
            throw new IllegalStateException("Tenant context not found");
        }

        // Verificar si ya existe un técnico para este usuario IAM en este tenant
        if (technicianRepository.existsByIamUserIdValueAndTenantIdValue(
                command.iamUserId().getValue(), tenantIdValue)) {
            throw new RuntimeException("Technician already exists for IAM user: " + command.iamUserId().getValue());
        }

        // Crear el técnico
        Technician technician = new Technician(
            command.iamUserId(),
            command.username(),
            command.firstName(),
            command.lastName(),
            command.email(),
            new TenantId(tenantIdValue)
        );

        Technician savedTechnician = technicianRepository.save(technician);
        logger.info("Technician created successfully with ID: {}", savedTechnician.getId());

        // Publicar evento de dominio
        eventPublisher.publishEvent(new TechnicianCreatedEvent(
            savedTechnician.getId(),
            command.iamUserId(),
            tenantIdValue
        ));

        return savedTechnician;
    }
}