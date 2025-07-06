package com.wiwitech.mecanetbackend.workorders.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "work_order_comments")
@NoArgsConstructor
public class WorkOrderComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long authorUserId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private LocalDateTime commentedAt;

    public WorkOrderComment(Long authorUserId, String text) {
        if (text == null || text.isBlank())
            throw new IllegalArgumentException("Comment text cannot be blank");
        this.authorUserId = authorUserId;
        this.text = text;
        this.commentedAt = LocalDateTime.now();
    }
} 