package com.wiwitech.mecanetbackend.competency.domain.model.events;
public record SkillCreatedEvent(Long skillId, Long tenantId, String name) {}