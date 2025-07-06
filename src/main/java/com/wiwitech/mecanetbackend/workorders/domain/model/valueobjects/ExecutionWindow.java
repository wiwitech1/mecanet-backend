package com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor
public class ExecutionWindow {

    @Column(name = "real_start")
    private LocalDateTime start;

    @Column(name = "real_end")
    private LocalDateTime end;

    public ExecutionWindow(LocalDateTime start, LocalDateTime end) {
        if (start == null)
            throw new IllegalArgumentException("start cannot be null");
        if (end != null && end.isBefore(start))
            throw new IllegalArgumentException("end must be after start");
        this.start = start;
        this.end = end;
    }

    public void end(LocalDateTime end) {
        if (end == null || end.isBefore(start))
            throw new IllegalArgumentException("Invalid end time");
        this.end = end;
    }
} 