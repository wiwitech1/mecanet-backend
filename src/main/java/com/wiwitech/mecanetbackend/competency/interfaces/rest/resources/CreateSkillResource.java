package com.wiwitech.mecanetbackend.competency.interfaces.rest.resources;

/** Payload used to create a skill. */
public record CreateSkillResource(
        String name,
        String description,
        String category
) {}