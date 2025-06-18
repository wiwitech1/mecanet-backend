package com.wiwitech.mecanetbackend.assetmanagment.application.internal.queryservices;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.ProductionLine;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetProductionLineByIdQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetProductionLinesByPlantQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetRunningProductionLinesQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.services.ProductionLineQueryService;
import com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories.ProductionLineRepository;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Production line query service implementation
 * Handles production line query operations with multitenancy support
 */
@Service
@Slf4j
public class ProductionLineQueryServiceImpl implements ProductionLineQueryService {
    
    private final ProductionLineRepository productionLineRepository;
    
    public ProductionLineQueryServiceImpl(ProductionLineRepository productionLineRepository) {
        this.productionLineRepository = productionLineRepository;
    }
    
    @Override
    public List<ProductionLine> handle(GetRunningProductionLinesQuery query) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        log.info("Getting running production lines for tenant {}", tenantId);
        return productionLineRepository.findRunningByTenantId(tenantId);
    }
    
    @Override
    public Optional<ProductionLine> handle(GetProductionLineByIdQuery query) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        log.info("Getting production line by ID {} for tenant {}", query.productionLineId(), tenantId);
        return productionLineRepository.findByIdAndTenantId(query.productionLineId(), tenantId);
    }
    
    @Override
    public List<ProductionLine> handle(GetProductionLinesByPlantQuery query) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        log.info("Getting production lines by plant {} for tenant {}", query.plantId(), tenantId);
        return productionLineRepository.findByPlantIdAndTenantId(query.plantId(), tenantId);
    }
}