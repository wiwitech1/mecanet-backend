package com.wiwitech.mecanetbackend.iam.domain.model.aggregates;

import com.wiwitech.mecanetbackend.iam.domain.model.entities.Role;
import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User aggregate root
 * This class represents the aggregate root for the User entity.
 *
 * @see AuditableAbstractAggregateRoot
 */
@Getter
@Setter
@Entity
public class User extends AuditableAbstractAggregateRoot<User> {

    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String password;

    @Embedded
    private EmailAddress email;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private Long tenantId;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable( name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User() {
        this.roles = new HashSet<>();
        this.active = true;
    }
    public User(String username, String password, EmailAddress email, String firstName, String lastName,Long tenantId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.tenantId = tenantId;
        this.roles = new HashSet<>();
    }

    public User(String username, String password, EmailAddress email, 
                String firstName, String lastName, Long tenantId, List<Role> roles) {
        this(username, password, email, firstName, lastName, tenantId);
        addRoles(roles);
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
    
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
    
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public String getEmail() {
        return email != null ? email.value() : null;
    }
    
    public void setEmail(String emailValue) {
        this.email = emailValue != null ? new EmailAddress(emailValue) : null;
    }
    

    /**
     * Add a role to the user
     * @param role the role to add
     * @return the user with the added role
     */
    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    /**
     * Add a list of roles to the user
     * @param roles the list of roles to add
     * @return the user with the added roles
     */
    public User addRoles(List<Role> roles) {
        var validatedRoleSet = Role.validateRoleSet(roles);
        this.roles.addAll(validatedRoleSet);
        return this;
    }

}

