package com.pfa.elearning.dto.request;

import com.pfa.elearning.model.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role; // STUDENT or TEACHER
    
    private String niveau;
    private String domaineInteret;
    private String autreDomaineInteret;
    private String objectif;
    private String autreObjectif;
}
