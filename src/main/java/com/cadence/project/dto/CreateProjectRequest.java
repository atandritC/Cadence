package com.cadence.project.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProjectRequest(

        @NotBlank(message = "Project name is required.") String name,

        String description,

        LocalDate dueDate,

        @NotNull(message = "managerId is required.") Long managerId

) {
}
