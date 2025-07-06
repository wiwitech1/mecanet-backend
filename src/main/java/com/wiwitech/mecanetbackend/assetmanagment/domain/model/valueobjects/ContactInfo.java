package com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects;

import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.PhoneNumber;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Embeddable
@Getter
public class ContactInfo {
    @Embedded
    private PhoneNumber phone;
    
    @Embedded
    private EmailAddress email;
    
    @Size(max = 100)
    private String contactPerson;
    
    // Constructor completo
    public ContactInfo(PhoneNumber phone, EmailAddress email) {
        validatePhone(phone);
        validateEmail(email);
        
        this.phone = phone;
        this.email = email;
        this.contactPerson = null; // Opcional
    }
    
    // Constructor con persona de contacto
    public ContactInfo(PhoneNumber phone, EmailAddress email, String contactPerson) {
        validatePhone(phone);
        validateEmail(email);
        validateContactPerson(contactPerson);
        
        this.phone = phone;
        this.email = email;
        this.contactPerson = contactPerson;
    }
    
    // Constructor vacío para JPA
    protected ContactInfo() {}
    
    // Validaciones privadas
    private void validatePhone(PhoneNumber phone) {
        if (phone == null) {
            throw new IllegalArgumentException("Phone cannot be null");
        }
    }
    
    private void validateEmail(EmailAddress email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
    }
    
    private void validateContactPerson(String contactPerson) {
        if (contactPerson != null && contactPerson.length() > 100) {
            throw new IllegalArgumentException("Contact person name cannot exceed 100 characters");
        }
    }
}