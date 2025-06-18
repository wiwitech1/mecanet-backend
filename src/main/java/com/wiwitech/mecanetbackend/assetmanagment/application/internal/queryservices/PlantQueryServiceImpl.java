package com.wiwitech.mecanetbackend.assetmanagment.application.internal.queryservices;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.Plant;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetActivePlantsQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetAllPlantsQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetPlantByIdQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.services.PlantQueryService;
import com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories.PlantRepository;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Plant query service implementation
 * Handles plant query operations with multitenancy support
 */
@Service
@Slf4j
public class PlantQueryServiceImpl implements PlantQueryService {
    
    private final PlantRepository plantRepository;
    
    public PlantQueryServiceImpl(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }
    
    @Override
    public List<Plant> handle(GetAllPlantsQuery query) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        log.info("Getting all plants for tenant {}", tenantId);
        return plantRepository.findAllByTenantId(tenantId);
    }
    
    @Override
    public List<Plant> handle(GetActivePlantsQuery query) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        log.info("Getting active plants for tenant {}", tenantId);
        return plantRepository.findAllByTenantIdAndIsActiveTrue(tenantId);
    }
    
    @Override
    public Optional<Plant> handle(GetPlantByIdQuery query) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        log.info("Getting plant by ID {} for tenant {}", query.plantId(), tenantId);
        return plantRepository.findByIdAndTenantId(query.plantId(), tenantId);
    }
}