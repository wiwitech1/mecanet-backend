package com.wiwitech.mecanetbackend.competency.domain.model.commands;
public record UpdateSkillCommand(Long skillId, String name, String description, String category) {}