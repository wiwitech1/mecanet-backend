package com.wiwitech.mecanetbackend.assetmanagment.application.internal.commandservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.Plant;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.ActivatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.CreatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.DeactivatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.UpdatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.services.PlantCommandService;
import com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories.PlantRepository;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;

/**
 * Plant command service implementation
 * Handles plant command operations with multitenancy support
 */
@Service
public class PlantCommandServiceImpl implements PlantCommandService {
    
    private static final Logger logger = LoggerFactory.getLogger(PlantCommandServiceImpl.class);
    
    private final PlantRepository plantRepository;
    
    public PlantCommandServiceImpl(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }
    
    @Override
    @Transactional
    public Plant handle(CreatePlantCommand command) {
        logger.info("Creating new plant: {}", command.name());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        // Validar que no exista una planta con el mismo nombre en el tenant
        if (plantRepository.existsByNameAndTenantId(command.name(), tenantId)) {
            throw new RuntimeException("A plant with this name already exists in this organization");
        }
        
        Plant plant = Plant.createNew(
            command.name(),
            command.location(),
            command.contactInfo(),
            new TenantId(tenantId)
        );
        
        Plant savedPlant = plantRepository.save(plant);
        logger.info("Plant created successfully with ID: {}", savedPlant.getId());
        
        return savedPlant;
    }
    
    @Override
    @Transactional
    public Plant handle(UpdatePlantCommand command) {
        logger.info("Updating plant with ID: {}", command.plantId());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        Plant plant = plantRepository.findByIdAndTenantId(command.plantId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));
        
        // Validar nombre único si cambió
        if (!plant.getName().equals(command.name()) && 
            plantRepository.existsByNameAndTenantId(command.name(), tenantId)) {
            throw new RuntimeException("A plant with this name already exists in this organization");
        }
        
        plant.updateInfo(command.name(), command.location(), command.contactInfo());
        
        Plant savedPlant = plantRepository.save(plant);
        logger.info("Plant updated successfully: {}", savedPlant.getName());
        
        return savedPlant;
    }
    
    @Override
    @Transactional
    public Plant handle(ActivatePlantCommand command) {
        logger.info("Activating plant with ID: {}", command.plantId());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        Plant plant = plantRepository.findByIdAndTenantId(command.plantId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));
        
        plant.activate();
        
        Plant savedPlant = plantRepository.save(plant);
        logger.info("Plant activated successfully: {}", savedPlant.getName());
        
        return savedPlant;
    }
    
    @Override
    @Transactional
    public Plant handle(DeactivatePlantCommand command) {
        logger.info("Deactivating plant with ID: {}", command.plantId());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        Plant plant = plantRepository.findByIdAndTenantId(command.plantId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));
        
        // Validar que no tenga líneas de producción activas
        long activeProductionLines = plantRepository.countActiveProductionLinesByPlantIdAndTenantId(command.plantId(), tenantId);
        if (activeProductionLines > 0) {
            throw new RuntimeException("Cannot deactivate plant with active production lines. Stop all production lines first.");
        }
        
        plant.deactivate();
        
        Plant savedPlant = plantRepository.save(plant);
        logger.info("Plant deactivated successfully: {}", savedPlant.getName());
        
        return savedPlant;
    }
}