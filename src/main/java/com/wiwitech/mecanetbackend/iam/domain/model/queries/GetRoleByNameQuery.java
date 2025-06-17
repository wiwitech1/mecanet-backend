package com.wiwitech.mecanetbackend.iam.domain.model.queries;

import com.wiwitech.mecanetbackend.iam.domain.model.valueobjects.Roles;


public record GetRoleByNameQuery(Roles name) {
}