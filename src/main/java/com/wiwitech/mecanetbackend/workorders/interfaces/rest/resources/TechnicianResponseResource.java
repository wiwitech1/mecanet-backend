package com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources;

import java.util.Set;

public class TechnicianResponseResource {
    public Long id;
    public Long iamUserId;
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String phoneNumber;
    public String status;
    public String shift;
    public Set<Long> skills;
    public Long supervisorId;
    public Long tenantId;
} 