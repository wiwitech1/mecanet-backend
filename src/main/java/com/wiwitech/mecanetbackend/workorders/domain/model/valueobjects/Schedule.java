package com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Embeddable
@NoArgsConstructor
public class Schedule {

    @Column(name = "scheduled_date")
    private LocalDate date;

    @Column(name = "scheduled_start_time")
    private LocalTime startTime;

    @Column(name = "scheduled_duration_hours")
    private Integer durationHours;

    public Schedule(LocalDate date, LocalTime startTime, Integer durationHours) {
        if (date == null || startTime == null || durationHours == null || durationHours <= 0)
            throw new IllegalArgumentException("Invalid schedule parameters");
        this.date = date;
        this.startTime = startTime;
        this.durationHours = durationHours;
    }
} 