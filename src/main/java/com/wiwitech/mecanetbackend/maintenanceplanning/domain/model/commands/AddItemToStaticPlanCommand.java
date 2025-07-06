package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.commands;

public record AddItemToStaticPlanCommand(Long planId, int dayIndex) {}