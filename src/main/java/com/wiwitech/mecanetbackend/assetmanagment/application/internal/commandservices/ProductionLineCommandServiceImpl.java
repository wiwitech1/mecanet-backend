package com.wiwitech.mecanetbackend.assetmanagment.application.internal.commandservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.ProductionLine;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.CreateProductionLineCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.StartProductionCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.StopProductionCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.services.ProductionLineCommandService;
import com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories.PlantRepository;
import com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories.ProductionLineRepository;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;

/**
 * Production line command service implementation
 * Handles production line command operations with multitenancy support
 */
@Service
public class ProductionLineCommandServiceImpl implements ProductionLineCommandService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductionLineCommandServiceImpl.class);
    
    private final ProductionLineRepository productionLineRepository;
    private final PlantRepository plantRepository;
    
    public ProductionLineCommandServiceImpl(ProductionLineRepository productionLineRepository,
                                          PlantRepository plantRepository) {
        this.productionLineRepository = productionLineRepository;
        this.plantRepository = plantRepository;
    }
    
    @Override
    @Transactional
    public ProductionLine handle(CreateProductionLineCommand command) {
        logger.info("Creating new production line: {}", command.name());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        // Validar que la planta existe y está activa en el tenant
        plantRepository.findByIdAndTenantId(command.plantId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));
        
        // Validar que no exista una línea de producción con el mismo nombre en la planta
        if (productionLineRepository.existsByNameAndPlantIdAndTenantId(command.name(), command.plantId(), tenantId)) {
            throw new RuntimeException("A production line with this name already exists in this plant");
        }
        
        ProductionLine productionLine = ProductionLine.createNew(
            command.name(),
            command.code(),
            command.capacity(),
            command.plantId(),
            new TenantId(tenantId)
        );
        
        ProductionLine savedProductionLine = productionLineRepository.save(productionLine);
        logger.info("Production line created successfully with ID: {}", savedProductionLine.getId());
        
        return savedProductionLine;
    }
    
    @Override
    @Transactional
    public ProductionLine handle(StartProductionCommand command) {
        logger.info("Starting production on line {}", command.productionLineId());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        ProductionLine productionLine = productionLineRepository.findByIdAndTenantId(command.productionLineId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Production line not found"));
        
        // Validar que tenga máquinas asignadas
        long machineCount = productionLineRepository.countMachinesByProductionLineIdAndTenantId(command.productionLineId(), tenantId);
        if (machineCount == 0) {
            throw new RuntimeException("Cannot start production without assigned machines");
        }
        
        productionLine.startProduction();
        
        ProductionLine savedProductionLine = productionLineRepository.save(productionLine);
        logger.info("Production started successfully on line: {}", savedProductionLine.getName());
        
        return savedProductionLine;
    }
    
    @Override
    @Transactional
    public ProductionLine handle(StopProductionCommand command) {
        logger.info("Stopping production on line {}", command.productionLineId());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        ProductionLine productionLine = productionLineRepository.findByIdAndTenantId(command.productionLineId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Production line not found"));
        
        productionLine.stopProduction(command.reason());
        
        ProductionLine savedProductionLine = productionLineRepository.save(productionLine);
        logger.info("Production stopped successfully on line: {} with reason: {}", 
                   savedProductionLine.getName(), command.reason());
        
        return savedProductionLine;
    }
}