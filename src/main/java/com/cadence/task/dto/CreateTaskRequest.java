package com.cadence.task.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateTaskRequest(

        @NotBlank(message = "Title is required.") String title,

        String description,

        @PositiveOrZero(message = "Story points cannot be negative.") Integer storyPoints,

        LocalDate dueDate,

        @NotNull(message = "projectId is required.") Long projectId,

        Long assigneeId

) {
}
