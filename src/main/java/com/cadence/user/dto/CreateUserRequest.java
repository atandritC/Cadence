package com.cadence.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(

        @NotBlank(message = "Email address is required.") @Email(message = "Please provide a valid email address.") String email,

        @NotBlank(message = "Name is required.") String fullName

) {
}
