package com.cadence.project.dto;

import com.cadence.project.ProjectStatus;

public record ProjectResponse(

        Long id,
        String name,
        String description,
        ProjectStatus status,
        java.time.LocalDate dueDate,
        Integer totalStoryPoints,
        Long remainingDays,
        boolean overdue,
        Long managerId,
        java.util.Set<Long> memberIds,
        java.time.Instant createdAt

) {
}
