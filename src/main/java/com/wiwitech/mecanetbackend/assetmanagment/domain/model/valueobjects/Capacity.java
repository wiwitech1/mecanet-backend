package com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Capacity {
    
    @NotNull
    @Min(1)
    private Integer unitsPerHour;
    
    @NotNull
    @Min(1)
    private Integer maxUnitsPerDay;
    
    // Constructor completo
    public Capacity(Integer unitsPerHour, Integer maxUnitsPerDay) {
        validateUnitsPerHour(unitsPerHour);
        validateMaxUnitsPerDay(maxUnitsPerDay);
        validateConsistency(unitsPerHour, maxUnitsPerDay);
        
        this.unitsPerHour = unitsPerHour;
        this.maxUnitsPerDay = maxUnitsPerDay;
    }
    
    // Constructor vacío para JPA
    protected Capacity() {}
    
    // ✅ MÉTODO QUE FALTA - isValid()
    public boolean isValid() {
        return unitsPerHour != null && 
               maxUnitsPerDay != null && 
               unitsPerHour > 0 && 
               maxUnitsPerDay > 0 &&
               isCapacityConsistent();
    }
    
    // ✅ Métodos de validación y cálculo
    public boolean canHandle(int requiredUnits) {
        return isValid() && requiredUnits <= unitsPerHour;
    }
    
    public boolean canHandleDaily(int requiredUnitsPerDay) {
        return isValid() && requiredUnitsPerDay <= maxUnitsPerDay;
    }
    
    public int calculateMaxUnitsPerShift(int hoursPerShift) {
        if (!isValid() || hoursPerShift <= 0) {
            return 0;
        }
        return unitsPerHour * hoursPerShift;
    }
    
    private boolean isCapacityConsistent() {
        // Validar que maxUnitsPerDay sea razonable comparado con unitsPerHour
        // Asumiendo máximo 24 horas por día
        return maxUnitsPerDay <= (unitsPerHour * 24);
    }
    
    // ✅ Validaciones privadas
    private void validateUnitsPerHour(Integer unitsPerHour) {
        if (unitsPerHour == null || unitsPerHour <= 0) {
            throw new IllegalArgumentException("Units per hour must be a positive number");
        }
    }
    
    private void validateMaxUnitsPerDay(Integer maxUnitsPerDay) {
        if (maxUnitsPerDay == null || maxUnitsPerDay <= 0) {
            throw new IllegalArgumentException("Max units per day must be a positive number");
        }
    }
    
    private void validateConsistency(Integer unitsPerHour, Integer maxUnitsPerDay) {
        if (unitsPerHour != null && maxUnitsPerDay != null) {
            // Verificar que la capacidad diaria sea consistente (máximo 24 horas/día)
            if (maxUnitsPerDay > (unitsPerHour * 24)) {
                throw new IllegalArgumentException(
                    "Max units per day (" + maxUnitsPerDay + ") cannot exceed " +
                    "units per hour (" + unitsPerHour + ") * 24 hours = " + (unitsPerHour * 24)
                );
            }
        }
    }
    
    // ✅ Getters
    public Integer getUnitsPerHour() {
        return unitsPerHour;
    }
    
    public Integer getMaxUnitsPerDay() {
        return maxUnitsPerDay;
    }
    
    // ✅ Factory method
    public static Capacity of(Integer unitsPerHour, Integer maxUnitsPerDay) {
        return new Capacity(unitsPerHour, maxUnitsPerDay);
    }
    
    // ✅ Equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Capacity capacity = (Capacity) o;
        return unitsPerHour.equals(capacity.unitsPerHour) && 
               maxUnitsPerDay.equals(capacity.maxUnitsPerDay);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(unitsPerHour, maxUnitsPerDay);
    }
    
    @Override
    public String toString() {
        return "Capacity{" +
                "unitsPerHour=" + unitsPerHour +
                ", maxUnitsPerDay=" + maxUnitsPerDay +
                '}';
    }
}