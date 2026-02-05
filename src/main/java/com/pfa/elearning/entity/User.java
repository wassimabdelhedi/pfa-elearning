package com.pfa.elearning.entity;

import com.pfa.elearning.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * User entity representing all users in the system.
 * Implements UserDetails for Spring Security integration.
 * 
 * Supports three roles: ADMIN, TEACHER, LEARNER
 * Each role has specific permissions and capabilities.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Learner-specific fields
    @Column
    private String level;  // e.g., "Beginner", "Intermediate", "Advanced"

    @Column(columnDefinition = "TEXT")
    private String goals;  // Learning goals for learners

    // Teacher-specific fields
    @Column(columnDefinition = "TEXT")
    private String bio;  // Teacher biography

    @Column
    private String specialization;  // Teacher's area of expertise

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Course> coursesCreated;  // For teachers

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments;  // For learners

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LearningHistory> learningHistory;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Score> scores;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TimeSpent> timeSpentRecords;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserPreference userPreference;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
