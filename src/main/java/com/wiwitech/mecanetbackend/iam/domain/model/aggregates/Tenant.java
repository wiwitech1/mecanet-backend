package com.wiwitech.mecanetbackend.iam.domain.model.aggregates;


import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.PhoneNumber;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "tenants")
public class Tenant extends AuditableAbstractAggregateRoot<Tenant> {

    @NotBlank
    @Size(max = 11)
    @Column(unique = true, nullable = false)
    private String ruc;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String legalName;

    @NotBlank
    @Size(max = 20)
    @Column(unique = true, nullable = false)
    private String code;

    @Size(max = 100)
    private String commercialName;

    @Size(max = 200)
    private String address;

    @Size(max = 50)
    private String city;

    @Size(max = 50)
    private String country;

    @Embedded
    private PhoneNumber phoneNumber;

    @Embedded
    private EmailAddress email;

    @Size(max = 100)
    private String website;

    @Column(nullable = false)
    private Boolean active;

    public Tenant() {
        this.active = true;
    }

    public Tenant(String ruc, String legalName, String commercialName,
                  String address, String city, String country,
                  PhoneNumber phone, EmailAddress email, String website) {
        this();
        this.ruc = ruc;
        this.legalName = legalName;
        this.code = generateCodeFromRuc(ruc);
        this.commercialName = commercialName;
        this.address = address;
        this.city = city;
        this.country = country;
        this.phoneNumber = phone;
        this.email = email;
        this.website = website;
    }

    /**
     * Generate a unique code from RUC
     * @param ruc the RUC
     * @return generated code
     */
    private String generateCodeFromRuc(String ruc) {
        // Simple code generation: TENANT_ + last 6 digits of RUC
        if (ruc != null && ruc.length() >= 6) {
            return "TENANT_" + ruc.substring(ruc.length() - 6);
        }
        return "TENANT_" + System.currentTimeMillis() % 1000000;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getDisplayName() {
        return commercialName != null && !commercialName.isEmpty()
                ? commercialName
                : legalName;
    }

    public String getIdentifier() {
        return this.code;
    }

    public String getEmail() {
        return email != null ? email.value() : null;
    }

    public String getPhone() {
        return phoneNumber != null ? phoneNumber.value() : null;
    }
}
