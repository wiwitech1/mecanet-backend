package com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class MaintenanceInfo {
    private LocalDateTime lastMaintenanceDate;
    private LocalDateTime nextMaintenanceDate;
    private Integer maintenanceIntervalDays;
    
    // ✅ Constructor
    public MaintenanceInfo(LocalDateTime lastMaintenanceDate, 
                           LocalDateTime nextMaintenanceDate, 
                           Integer maintenanceIntervalDays) {
        this.lastMaintenanceDate = lastMaintenanceDate;
        this.nextMaintenanceDate = nextMaintenanceDate;
        this.maintenanceIntervalDays = maintenanceIntervalDays != null ? maintenanceIntervalDays : 30;
    }
    
    // Constructor vacío para JPA
    protected MaintenanceInfo() {}
    
    // ✅ Factory methods
    public static MaintenanceInfo createNew() {
        return new MaintenanceInfo(null, null, 30);
    }
    
    // ✅ Métodos inmutables
    public MaintenanceInfo withLastMaintenanceDate(LocalDateTime date) {
        LocalDateTime nextDate = date.plusDays(maintenanceIntervalDays);
        return new MaintenanceInfo(date, nextDate, maintenanceIntervalDays);
    }
    
    public MaintenanceInfo withNextMaintenanceDate() {
        LocalDateTime nextDate = LocalDateTime.now().plusDays(maintenanceIntervalDays);
        return new MaintenanceInfo(lastMaintenanceDate, nextDate, maintenanceIntervalDays);
    }
    
    public boolean isMaintenanceDue() {
        return nextMaintenanceDate != null && 
               LocalDateTime.now().isAfter(nextMaintenanceDate);
    }
} 