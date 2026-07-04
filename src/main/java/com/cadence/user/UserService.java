package com.cadence.user;

import java.util.List;

import com.cadence.user.dto.UserResponse;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

}
