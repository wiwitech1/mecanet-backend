package com.wiwitech.mecanetbackend.iam.infrastructure.authorization.sfs.model;

import com.wiwitech.mecanetbackend.iam.domain.model.aggregates.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This class is responsible for providing the user details to the Spring Security framework.
 * It implements the UserDetails interface with multitenancy support.
 */
@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String username;
    @JsonIgnore
    private final String password;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Long tenantId;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * This constructor initializes the UserDetailsImpl object.
     * @param id The user ID.
     * @param username The username.
     * @param password The password.
     * @param email The email.
     * @param firstName The first name.
     * @param lastName The last name.
     * @param tenantId The tenant ID.
     * @param enabled Whether the user is enabled.
     * @param authorities The authorities.
     */
    public UserDetailsImpl(Long id, String username, String password, String email, 
                          String firstName, String lastName, Long tenantId, 
                          boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.tenantId = tenantId;
        this.authorities = authorities;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = enabled;
    }

    /**
     * This method is responsible for building the UserDetailsImpl object from the User object.
     * @param user The user object.
     * @return The UserDetailsImpl object.
     */
    public static UserDetailsImpl build(User user) {
        var authorities = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName().name())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getTenantId(),
                user.getActive(),
                authorities);
    }

    /**
     * Get the full name of the user
     * @return The full name
     */
    public String getFullName() {
        return String.format("%s %s", firstName, lastName).trim();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
