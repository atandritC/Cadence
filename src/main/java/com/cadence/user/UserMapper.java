package com.cadence.user;

import com.cadence.user.dto.CreateUserRequest;
import com.cadence.user.dto.UserResponse;

public final class UserMapper {

    private UserMapper() {
    }

    // DTO -> Entity
    public static User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        return user;
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
