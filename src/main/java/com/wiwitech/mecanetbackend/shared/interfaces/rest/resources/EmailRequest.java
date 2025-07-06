package com.wiwitech.mecanetbackend.shared.interfaces.rest.resources;

public record EmailRequest(
    String to,
    String title,
    String description,
    String buttonText,
    String buttonUrl
) {}