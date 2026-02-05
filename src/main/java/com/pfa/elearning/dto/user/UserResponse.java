package com.pfa.elearning.dto.user;

import com.pfa.elearning.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user response data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private Boolean enabled;
    private LocalDateTime createdAt;

    // Learner-specific fields
    private String level;
    private String goals;

    // Teacher-specific fields
    private String bio;
    private String specialization;
}
