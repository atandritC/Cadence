package com.cadence.project.dto;

import com.cadence.project.ProjectStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateProjectStatusRequest(

        @NotNull(message = "Status is required.") ProjectStatus status

) {

}
