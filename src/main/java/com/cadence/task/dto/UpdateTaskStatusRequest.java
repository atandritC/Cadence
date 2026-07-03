package com.cadence.task.dto;

import com.cadence.task.TaskStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(

        @NotNull(message = "Status is required.") TaskStatus status

) {
}
