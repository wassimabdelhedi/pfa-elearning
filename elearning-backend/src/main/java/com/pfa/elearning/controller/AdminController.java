package com.pfa.elearning.controller;

import com.pfa.elearning.model.Category;
import com.pfa.elearning.model.Role;
import com.pfa.elearning.model.User;
import com.pfa.elearning.repository.CategoryRepository;
import com.pfa.elearning.repository.CourseRepository;
import com.pfa.elearning.repository.EnrollmentRepository;
import com.pfa.elearning.repository.UserRepository;
import com.pfa.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CategoryRepository categoryRepository;

    // ========== DASHBOARD ==========

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        long totalStudents = userRepository.findByRole(Role.STUDENT).size();
        long totalTeachers = userRepository.findByRole(Role.TEACHER).size();
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();

        return ResponseEntity.ok(Map.of(
                "totalStudents", totalStudents,
                "totalTeachers", totalTeachers,
                "totalCourses", totalCourses,
                "totalEnrollments", totalEnrollments
        ));
    }

    // ========== USER MANAGEMENT ==========

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<Map<String, Object>> result = users.stream().map(u -> Map.<String, Object>of(
                "id", u.getId(),
                "fullName", u.getFullName(),
                "email", u.getEmail(),
                "role", u.getRole().name(),
                "active", u.isActive(),
                "createdAt", u.getCreatedAt().toString()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/users/{id}/toggle-active")
    public ResponseEntity<Map<String, String>> toggleUserActive(@PathVariable Long id) {
        userService.toggleUserActive(id);
        return ResponseEntity.ok(Map.of("message", "User status updated"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ========== CATEGORY MANAGEMENT ==========

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String description = body.getOrDefault("description", "");
        String icon = body.getOrDefault("icon", "📚");

        if (categoryRepository.existsByName(name)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Category category = Category.builder()
                .name(name)
                .description(description)
                .icon(icon)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryRepository.save(category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
