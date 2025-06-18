package com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Embeddable
@Getter
public class Location {
    @Size(max = 200)
    private String address;
    
    @Size(max = 50)
    private String city;
    
    @Size(max = 50)
    private String country;
    
    @Size(max = 20)
    private String zipCode;
    
    // Constructor completo
    public Location(String address, String city, String country) {
        validateAddress(address);
        validateCity(city);
        validateCountry(country);
        
        this.address = address;
        this.city = city;
        this.country = country;
        this.zipCode = null; // Opcional
    }
    
    // Constructor con zipCode
    public Location(String address, String city, String country, String zipCode) {
        validateAddress(address);
        validateCity(city);
        validateCountry(country);
        validateZipCode(zipCode);
        
        this.address = address;
        this.city = city;
        this.country = country;
        this.zipCode = zipCode;
    }
    
    // Constructor vacío para JPA
    protected Location() {}
    
    // Validaciones privadas
    private void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        if (address.length() > 200) {
            throw new IllegalArgumentException("Address cannot exceed 200 characters");
        }
    }
    
    private void validateCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        if (city.length() > 50) {
            throw new IllegalArgumentException("City cannot exceed 50 characters");
        }
    }
    
    private void validateCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be null or empty");
        }
        if (country.length() > 50) {
            throw new IllegalArgumentException("Country cannot exceed 50 characters");
        }
    }
    
    private void validateZipCode(String zipCode) {
        if (zipCode != null && zipCode.length() > 20) {
            throw new IllegalArgumentException("Zip code cannot exceed 20 characters");
        }
    }
    
    // Método de utilidad
    public String getFullAddress() {
        return String.format("%s, %s, %s", address, city, country);
    }
}