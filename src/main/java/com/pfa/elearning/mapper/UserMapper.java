package com.pfa.elearning.mapper;

import com.pfa.elearning.dto.user.UserResponse;
import com.pfa.elearning.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper for User entity to DTO conversions.
 */
@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .level(user.getLevel())
                .goals(user.getGoals())
                .bio(user.getBio())
                .specialization(user.getSpecialization())
                .build();
    }
}
