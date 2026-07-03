package com.cadence.task.dto;

import com.cadence.task.TaskStatus;

public record TaskResponse(

        Long id,
        String title,
        String description,
        TaskStatus status,
        Integer storyPoints,
        java.time.LocalDate dueDate,
        Long remainingDays,
        boolean overdue,
        Long projectId,
        Long assigneeId,
        java.time.Instant createdAt

) {
}
