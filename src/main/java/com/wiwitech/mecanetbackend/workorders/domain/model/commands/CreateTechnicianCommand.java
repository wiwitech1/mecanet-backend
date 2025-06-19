package com.wiwitech.mecanetbackend.workorders.domain.model.commands;

import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.UserId;

public record CreateTechnicianCommand(
        UserId iamUserId,
        String username,
        String firstName,
        String lastName,
        EmailAddress email
) {}