package com.wiwitech.mecanetbackend.workorders.application.internal.queryservices;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.SkillId;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.WorkOrderRepository;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.TechnicianRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkOrderValidationServiceImpl {
    
    private final WorkOrderRepository workOrderRepository;
    private final TechnicianRepository technicianRepository;
    
    public WorkOrderValidationServiceImpl(
            WorkOrderRepository workOrderRepository,
            TechnicianRepository technicianRepository) {
        this.workOrderRepository = workOrderRepository;
        this.technicianRepository = technicianRepository;
    }
    
    // === VALIDACIÓN BÁSICA ===
    
    public Map<String, Object> validateWorkOrder(String tenantId, String workOrderId) {
        Map<String, Object> validation = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        Long workOrderIdLong = Long.valueOf(workOrderId);
        
        Optional<WorkOrder> workOrderOpt = workOrderRepository.findByWorkOrderIdValueAndTenantIdValue(workOrderIdLong, tenantIdLong);
        
        if (workOrderOpt.isEmpty()) {
            errors.add("Work order not found");
            validation.put("isValid", false);
            validation.put("errors", errors);
            return validation;
        }
        
        WorkOrder workOrder = workOrderOpt.get();
        
        // Validaciones básicas
        if (workOrder.getTitle() == null || workOrder.getTitle().trim().isEmpty()) {
            errors.add("Title is required");
        }
        
        if (workOrder.getDescription() == null || workOrder.getDescription().trim().isEmpty()) {
            warnings.add("Description is empty");
        }
        
        if (workOrder.getMachineId() == null) {
            errors.add("Machine ID is required");
        }
        
        validation.put("isValid", errors.isEmpty());
        validation.put("errors", errors);
        validation.put("warnings", warnings);
        validation.put("workOrderId", workOrderId);
        
        return validation;
    }
    
    // === VALIDACIÓN DE REGLAS DE NEGOCIO ===
    
    public Map<String, Object> validateBusinessRules(String tenantId, String workOrderId) {
        Map<String, Object> validation = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        Long workOrderIdLong = Long.valueOf(workOrderId);
        
        Optional<WorkOrder> workOrderOpt = workOrderRepository.findByWorkOrderIdValueAndTenantIdValue(workOrderIdLong, tenantIdLong);
        
        if (workOrderOpt.isEmpty()) {
            errors.add("Work order not found");
            validation.put("isValid", false);
            validation.put("errors", errors);
            return validation;
        }
        
        WorkOrder workOrder = workOrderOpt.get();
        
        // Validar transiciones de estado
        if (workOrder.getStatus() == WorkOrderStatus.IN_EXECUTION) {
            // Verificar que hay técnicos asignados - simplificado
            warnings.add("Verify technicians are assigned for execution");
        }
        
        // Validar skills requeridas
        if (workOrder.getRequiredSkillIds() != null && !workOrder.getRequiredSkillIds().isEmpty()) {
            boolean hasAvailableTechniciansWithSkills = hasAvailableTechniciansWithRequiredSkills(
                    tenantId, workOrder.getRequiredSkillIds());
            
            if (!hasAvailableTechniciansWithSkills) {
                warnings.add("No available technicians with required skills");
            }
        }
        
        validation.put("isValid", errors.isEmpty());
        validation.put("errors", errors);
        validation.put("warnings", warnings);
        validation.put("workOrderId", workOrderId);
        
        return validation;
    }
    
    private boolean hasAvailableTechniciansWithRequiredSkills(String tenantId, Set<SkillId> requiredSkills) {
        Long tenantIdLong = Long.valueOf(tenantId);
        
        List<Technician> availableTechnicians = technicianRepository.findByCurrentStatusAndTenantIdValue(
                TechnicianStatus.AVAILABLE, tenantIdLong);
        
        return availableTechnicians.stream()
                .anyMatch(tech -> tech.getSkills().containsAll(requiredSkills));
    }
    
    // === VALIDACIÓN CONTEXTUAL ===
    
    public Map<String, Object> validateContext(String tenantId, String workOrderId, String context) {
        Map<String, Object> validation = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        Long workOrderIdLong = Long.valueOf(workOrderId);
        
        Optional<WorkOrder> workOrderOpt = workOrderRepository.findByWorkOrderIdValueAndTenantIdValue(workOrderIdLong, tenantIdLong);
        
        if (workOrderOpt.isEmpty()) {
            errors.add("Work order not found");
            validation.put("isValid", false);
            validation.put("errors", errors);
            return validation;
        }
        
        WorkOrder workOrder = workOrderOpt.get();
        
        switch (context.toUpperCase()) {
            case "PUBLISH":
                validateForPublish(workOrder, errors, warnings);
                break;
            case "EXECUTE":
                validateForExecution(workOrder, errors, warnings);
                break;
            case "COMPLETE":
                validateForCompletion(workOrder, errors, warnings);
                break;
            default:
                warnings.add("Unknown validation context: " + context);
        }
        
        validation.put("isValid", errors.isEmpty());
        validation.put("errors", errors);
        validation.put("warnings", warnings);
        validation.put("context", context);
        validation.put("workOrderId", workOrderId);
        
        return validation;
    }
    
    private void validateForPublish(WorkOrder workOrder, List<String> errors, List<String> warnings) {
        if (workOrder.getStatus() != WorkOrderStatus.NEW) {
            errors.add("Only NEW work orders can be published");
        }
        
        if (workOrder.getMaxTechnicians() == null || workOrder.getMaxTechnicians() <= 0) {
            errors.add("Max technicians must be specified for publishing");
        }
        
        warnings.add("Verify scheduled start time is specified");
    }
    
    private void validateForExecution(WorkOrder workOrder, List<String> errors, List<String> warnings) {
        if (workOrder.getStatus() != WorkOrderStatus.PENDING_EXECUTION) {
            errors.add("Only PENDING_EXECUTION work orders can be started");
        }
        
        warnings.add("Verify at least one technician is assigned");
        
        if (workOrder.getRequiredSkillIds() != null && !workOrder.getRequiredSkillIds().isEmpty()) {
            warnings.add("Verify assigned technicians have required skills");
        }
    }
    
    private void validateForCompletion(WorkOrder workOrder, List<String> errors, List<String> warnings) {
        if (workOrder.getStatus() != WorkOrderStatus.IN_EXECUTION) {
            errors.add("Only IN_EXECUTION work orders can be completed");
        }
        
        warnings.add("Verify work order has assigned technicians for completion");
    }
    
    // === VALIDACIÓN DE INTEGRIDAD DE DATOS ===
    
    public Map<String, Object> validateDataIntegrity(String tenantId, String workOrderId) {
        Map<String, Object> validation = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        Long workOrderIdLong = Long.valueOf(workOrderId);
        
        Optional<WorkOrder> workOrderOpt = workOrderRepository.findByWorkOrderIdValueAndTenantIdValue(workOrderIdLong, tenantIdLong);
        
        if (workOrderOpt.isEmpty()) {
            errors.add("Work order not found");
            validation.put("isValid", false);
            validation.put("errors", errors);
            return validation;
        }
        
        WorkOrder workOrder = workOrderOpt.get();
        
        // Validaciones básicas de integridad
        if (workOrder.getTenantId() == null) {
            errors.add("Tenant ID is missing");
        }
        
        if (workOrder.getWorkOrderId() == null) {
            errors.add("Work Order ID is missing");
        }
        
        if (workOrder.getMachineId() == null) {
            errors.add("Machine ID is missing");
        }
        
        // Validar fechas básicas
        if (workOrder.getCreatedAt() == null) {
            warnings.add("Created date is missing");
        }
        
        if (workOrder.getUpdatedAt() == null) {
            warnings.add("Updated date is missing");
        }
        
        validation.put("isValid", errors.isEmpty());
        validation.put("errors", errors);
        validation.put("warnings", warnings);
        validation.put("workOrderId", workOrderId);
        
        return validation;
    }
    
    // === VALIDACIÓN MASIVA ===
    
    public Map<String, Object> validateMultipleWorkOrders(String tenantId, List<String> workOrderIds) {
        Map<String, Object> validation = new HashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();
        int validCount = 0;
        int invalidCount = 0;
        
        for (String workOrderId : workOrderIds) {
            Map<String, Object> singleValidation = validateWorkOrder(tenantId, workOrderId);
            results.add(singleValidation);
            
            if ((Boolean) singleValidation.get("isValid")) {
                validCount++;
            } else {
                invalidCount++;
            }
        }
        
        validation.put("totalValidated", workOrderIds.size());
        validation.put("validCount", validCount);
        validation.put("invalidCount", invalidCount);
        validation.put("results", results);
        
        return validation;
    }
} 