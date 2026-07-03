package com.cadence.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cadence.user.dto.CreateUserRequest;
import com.cadence.user.dto.UserResponse;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        // Business rule: no two users can share an email
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email already in use: " + request.email());
        }
        User saved = userRepository.save(UserMapper.toEntity(request));
        return UserMapper.toResponse(saved);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found with id: " + id));
        return UserMapper.toResponse(user);
    }

}
