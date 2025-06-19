package com.wiwitech.mecanetbackend.iam.interfaces.rest.resources;

import java.util.List;

public record UserResource(Long id, String username, String name, String email, List<String> roles) {
}