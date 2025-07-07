package com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TechnicianRequestResource {
    @NotNull
    public Long iamUserId;
    @NotNull
    @Size(min = 3, max = 50)
    public String username;
    public String firstName;
    public String lastName;
    @NotNull
    public String email;
} 