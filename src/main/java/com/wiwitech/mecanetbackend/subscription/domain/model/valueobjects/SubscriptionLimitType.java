package com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects;

/**
 * Enum que define los tipos de límites de suscripción
 */
public enum SubscriptionLimitType {
    
    /**
     * Límite de usuarios por tenant
     */
    MAX_USERS("max_users"),
    
    /**
     * Límite de plantas por tenant
     */
    MAX_PLANTS("max_plants"),
    
    /**
     * Límite de máquinas por tenant
     */
    MAX_MACHINES("max_machines"),
    
    /**
     * Límite de líneas de producción por tenant
     */
    MAX_PRODUCTION_LINES("max_production_lines");
    
    private final String attributeName;
    
    SubscriptionLimitType(String attributeName) {
        this.attributeName = attributeName;
    }
    
    /**
     * Obtiene el nombre del atributo en la base de datos
     * @return Nombre del atributo
     */
    public String getAttributeName() {
        return attributeName;
    }
    
    /**
     * Obtiene el tipo de límite a partir del nombre del atributo
     * @param attributeName Nombre del atributo
     * @return Tipo de límite correspondiente
     */
    public static SubscriptionLimitType fromAttributeName(String attributeName) {
        for (SubscriptionLimitType type : values()) {
            if (type.attributeName.equals(attributeName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de límite no encontrado para: " + attributeName);
    }
} 