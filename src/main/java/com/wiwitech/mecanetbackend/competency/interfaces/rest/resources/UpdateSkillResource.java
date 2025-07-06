package com.wiwitech.mecanetbackend.competency.interfaces.rest.resources;

/** Payload used to update a skill. */
public record UpdateSkillResource(
        String name,
        String description,
        String category
) {}