package com.wiwitech.mecanetbackend.shared.interfaces.rest.resources;

import java.time.LocalDateTime;

public record EmailResponse(
    String message,
    LocalDateTime sentAt
) {}