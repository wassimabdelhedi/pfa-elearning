package com.pfa.elearning.dto.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @Email(message = "Invalid email format")
    private String email;

    private String firstName;
    private String lastName;

    // Learner-specific fields
    private String level;
    private String goals;

    // Teacher-specific fields
    private String bio;
    private String specialization;
}
