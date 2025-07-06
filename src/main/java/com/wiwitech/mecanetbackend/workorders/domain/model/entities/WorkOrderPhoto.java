package com.wiwitech.mecanetbackend.workorders.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "work_order_photos")
@NoArgsConstructor
public class WorkOrderPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String url;

    @Column(nullable = false)
    private Long authorUserId;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    public WorkOrderPhoto(String url, Long authorUserId) {
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("Photo url cannot be blank");
        this.url = url;
        this.authorUserId = authorUserId;
        this.uploadedAt = LocalDateTime.now();
    }
} 