package com.cadence.user;

import com.cadence.user.dto.UserResponse;

public final class UserMapper {

    private UserMapper() {
    }

    // Entity -> DTO
    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getCreatedAt());
    }

}
