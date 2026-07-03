package com.cadence.user;

import java.util.List;

import com.cadence.user.dto.CreateUserRequest;
import com.cadence.user.dto.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

}
