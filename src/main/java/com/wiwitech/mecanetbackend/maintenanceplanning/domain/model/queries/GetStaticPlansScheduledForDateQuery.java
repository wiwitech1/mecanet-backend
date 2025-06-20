package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.queries;

import java.time.LocalDate;

public record GetStaticPlansScheduledForDateQuery(LocalDate date) {}