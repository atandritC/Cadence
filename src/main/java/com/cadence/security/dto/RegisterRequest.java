package com.cadence.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank @Email String email,
        @NotBlank String fullName,
        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String password

) {
}
