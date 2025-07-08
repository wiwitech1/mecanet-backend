package com.wiwitech.mecanetbackend.workorders.application.internal.queryservices;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianStatus;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.WorkOrderRepository;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.TechnicianRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkOrderNotificationServiceImpl {
    
    private final WorkOrderRepository workOrderRepository;
    private final TechnicianRepository technicianRepository;
    
    public WorkOrderNotificationServiceImpl(
            WorkOrderRepository workOrderRepository,
            TechnicianRepository technicianRepository) {
        this.workOrderRepository = workOrderRepository;
        this.technicianRepository = technicianRepository;
    }
    
    // === NOTIFICACIONES BÁSICAS ===
    
    public List<Map<String, Object>> getBasicNotifications(String tenantId) {
        List<Map<String, Object>> notifications = new ArrayList<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Órdenes atrasadas
        List<WorkOrder> delayedOrders = workOrderRepository.findDelayedWorkOrders(tenantIdLong);
        for (WorkOrder order : delayedOrders) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "OVERDUE_ORDER");
            notification.put("workOrderId", order.getWorkOrderId().getValue());
            notification.put("title", order.getTitle());
            notification.put("priority", "HIGH");
            notification.put("machineId", order.getMachineId().getValue());
            notification.put("message", "Orden de trabajo atrasada: " + order.getTitle());
            notification.put("timestamp", LocalDateTime.now());
            notifications.add(notification);
        }
        
        // Órdenes pendientes de ejecución
        List<WorkOrder> pendingOrders = workOrderRepository.findByStatusAndTenantIdValue(WorkOrderStatus.PENDING_EXECUTION, tenantIdLong);
        for (WorkOrder order : pendingOrders) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "PENDING_EXECUTION");
            notification.put("workOrderId", order.getWorkOrderId().getValue());
            notification.put("title", order.getTitle());
            notification.put("priority", "MEDIUM");
            notification.put("machineId", order.getMachineId().getValue());
            notification.put("message", "Orden lista para ejecución: " + order.getTitle());
            notification.put("timestamp", LocalDateTime.now());
            notifications.add(notification);
        }
        
        return notifications;
    }
    
    // === NOTIFICACIONES POR TIPO ===
    
    public List<Map<String, Object>> getNotificationsByType(String tenantId, String type) {
        List<Map<String, Object>> notifications = new ArrayList<>();
        Long tenantIdLong = Long.valueOf(tenantId);
        
        switch (type.toUpperCase()) {
            case "OVERDUE":
                List<WorkOrder> delayedOrders = workOrderRepository.findDelayedWorkOrders(tenantIdLong);
                notifications = delayedOrders.stream()
                        .map(order -> {
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("type", "OVERDUE_ORDER");
                            notification.put("workOrderId", order.getWorkOrderId().getValue());
                            notification.put("title", order.getTitle());
                            notification.put("machineId", order.getMachineId().getValue());
                            notification.put("message", "Orden atrasada: " + order.getTitle());
                            notification.put("timestamp", LocalDateTime.now());
                            return notification;
                        })
                        .collect(Collectors.toList());
                break;
                
            case "PENDING":
                List<WorkOrder> pendingOrders = workOrderRepository.findByStatusAndTenantIdValue(WorkOrderStatus.PENDING_EXECUTION, tenantIdLong);
                notifications = pendingOrders.stream()
                        .map(order -> {
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("type", "PENDING_EXECUTION");
                            notification.put("workOrderId", order.getWorkOrderId().getValue());
                            notification.put("title", order.getTitle());
                            notification.put("machineId", order.getMachineId().getValue());
                            notification.put("message", "Orden pendiente: " + order.getTitle());
                            notification.put("timestamp", LocalDateTime.now());
                            return notification;
                        })
                        .collect(Collectors.toList());
                break;
                
            case "COMPLETED":
                List<WorkOrder> completedOrders = workOrderRepository.findByStatusAndTenantIdValue(WorkOrderStatus.COMPLETED, tenantIdLong);
                notifications = completedOrders.stream()
                        .map(order -> {
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("type", "COMPLETED_ORDER");
                            notification.put("workOrderId", order.getWorkOrderId().getValue());
                            notification.put("title", order.getTitle());
                            notification.put("machineId", order.getMachineId().getValue());
                            notification.put("message", "Orden completada: " + order.getTitle());
                            notification.put("timestamp", LocalDateTime.now());
                            return notification;
                        })
                        .collect(Collectors.toList());
                break;
        }
        
        return notifications;
    }
    
    // === NOTIFICACIONES POR PRIORIDAD ===
    
    public List<Map<String, Object>> getNotificationsByPriority(String tenantId, String priority) {
        List<Map<String, Object>> notifications = new ArrayList<>();
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Obtener órdenes activas y filtrar por prioridad conceptual
        List<WorkOrder> activeOrders = workOrderRepository.findByStatusAndTenantIdValue(WorkOrderStatus.IN_EXECUTION, tenantIdLong);
        List<WorkOrder> delayedOrders = workOrderRepository.findDelayedWorkOrders(tenantIdLong);
        
        // Combinar listas y crear notificaciones
        List<WorkOrder> allOrders = new ArrayList<>();
        allOrders.addAll(activeOrders);
        allOrders.addAll(delayedOrders);
        
        notifications = allOrders.stream()
                .map(order -> {
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("type", "PRIORITY_ORDER");
                    notification.put("workOrderId", order.getWorkOrderId().getValue());
                    notification.put("title", order.getTitle());
                    notification.put("machineId", order.getMachineId().getValue());
                    notification.put("priority", priority);
                    notification.put("message", "Orden " + priority + ": " + order.getTitle());
                    notification.put("timestamp", LocalDateTime.now());
                    return notification;
                })
                .collect(Collectors.toList());
        
        return notifications;
    }
    
    // === NOTIFICACIONES PARA TÉCNICOS ===
    
    public List<Map<String, Object>> getTechnicianNotifications(String tenantId) {
        List<Map<String, Object>> notifications = new ArrayList<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Técnicos disponibles
        List<Technician> availableTechnicians = technicianRepository.findByCurrentStatusAndTenantIdValue(TechnicianStatus.AVAILABLE, tenantIdLong);
        for (Technician technician : availableTechnicians) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "TECHNICIAN_AVAILABLE");
            notification.put("technicianId", technician.getId());
            notification.put("technicianName", technician.getFirstName() + " " + technician.getLastName());
            notification.put("message", "Técnico disponible: " + technician.getFirstName() + " " + technician.getLastName());
            notification.put("timestamp", LocalDateTime.now());
            notifications.add(notification);
        }
        
        return notifications;
    }
    
    // === NOTIFICACIONES CONTEXTUALES ===
    
    public List<Map<String, Object>> getContextualNotifications(String tenantId, String context) {
        List<Map<String, Object>> notifications = new ArrayList<>();
        Long tenantIdLong = Long.valueOf(tenantId);
        
        switch (context.toUpperCase()) {
            case "MAINTENANCE":
                List<WorkOrder> maintenanceOrders = workOrderRepository.findByStatusAndTenantIdValue(WorkOrderStatus.PUBLISHED, tenantIdLong);
                notifications = maintenanceOrders.stream()
                        .map(order -> {
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("type", "MAINTENANCE_NOTIFICATION");
                            notification.put("workOrderId", order.getWorkOrderId().getValue());
                            notification.put("title", order.getTitle());
                            notification.put("message", "Mantenimiento programado: " + order.getTitle());
                            notification.put("timestamp", LocalDateTime.now());
                            return notification;
                        })
                        .collect(Collectors.toList());
                break;
                
            case "EMERGENCY":
                List<WorkOrder> emergencyOrders = workOrderRepository.findDelayedWorkOrders(tenantIdLong);
                notifications = emergencyOrders.stream()
                        .map(order -> {
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("type", "EMERGENCY_NOTIFICATION");
                            notification.put("workOrderId", order.getWorkOrderId().getValue());
                            notification.put("title", order.getTitle());
                            notification.put("message", "Emergencia: " + order.getTitle());
                            notification.put("timestamp", LocalDateTime.now());
                            return notification;
                        })
                        .collect(Collectors.toList());
                break;
        }
        
        return notifications;
    }
    
    // === GESTIÓN DE ALERTAS ===
    
    public List<Map<String, Object>> getActiveAlerts(String tenantId) {
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Alertas por órdenes atrasadas
        List<WorkOrder> delayedOrders = workOrderRepository.findDelayedWorkOrders(tenantIdLong);
        for (WorkOrder order : delayedOrders) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("id", "DELAY_" + order.getWorkOrderId().getValue());
            alert.put("type", "DELAY_ALERT");
            alert.put("severity", "HIGH");
            alert.put("workOrderId", order.getWorkOrderId().getValue());
            alert.put("title", order.getTitle());
            alert.put("message", "Orden atrasada requiere atención inmediata");
            alert.put("timestamp", LocalDateTime.now());
            alert.put("isActive", true);
            alerts.add(alert);
        }
        
        return alerts;
    }
    
    // === RECORDATORIOS AUTOMÁTICOS ===
    
    public List<Map<String, Object>> getAutomaticReminders(String tenantId) {
        List<Map<String, Object>> reminders = new ArrayList<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Recordatorios para órdenes pendientes
        List<WorkOrder> pendingOrders = workOrderRepository.findByStatusAndTenantIdValue(WorkOrderStatus.PENDING_EXECUTION, tenantIdLong);
        for (WorkOrder order : pendingOrders) {
            Map<String, Object> reminder = new HashMap<>();
            reminder.put("id", "REMINDER_" + order.getWorkOrderId().getValue());
            reminder.put("type", "EXECUTION_REMINDER");
            reminder.put("workOrderId", order.getWorkOrderId().getValue());
            reminder.put("title", order.getTitle());
            reminder.put("message", "Recordatorio: Orden pendiente de ejecución");
            reminder.put("timestamp", LocalDateTime.now());
            reminder.put("nextReminder", LocalDateTime.now().plusHours(2));
            reminders.add(reminder);
        }
        
        return reminders;
    }
} 