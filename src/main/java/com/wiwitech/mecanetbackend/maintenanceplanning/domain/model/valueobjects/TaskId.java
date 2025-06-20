package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
public class TaskId {

    @Column(name = "task_id")
    private String value;

    public TaskId(UUID uuid) {
        this.value = uuid.toString();
    }

    @Override public String toString() { return value; }
}