package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor
public class PlanPeriod {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public PlanPeriod(LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start)) {
            throw new IllegalArgumentException("Rango de fechas inválido");
        }
        this.startDate = start;
        this.endDate   = end;
    }

    public boolean contains(LocalDate date) {
        return (date.isEqual(startDate) || date.isAfter(startDate))
            && (date.isEqual(endDate)   || date.isBefore(endDate));
    }
}