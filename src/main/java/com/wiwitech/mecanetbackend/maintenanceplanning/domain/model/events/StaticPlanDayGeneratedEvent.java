package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events;
import java.time.LocalDate;

public record StaticPlanDayGeneratedEvent(Long planId,
                                          LocalDate cycleStart,
                                          int dayIndex) {}
