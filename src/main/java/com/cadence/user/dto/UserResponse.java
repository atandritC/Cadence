package com.cadence.user.dto;

import java.time.Instant;

public record UserResponse(

        Long id,
        String email,
        String fullName,
        Instant createdAt

) {
}
