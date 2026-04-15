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

        List<Map<String, Object>> topTeachersList = userRepository.findTopTeachers().stream().map(u -> Map.<String, Object>of(
                "id", u.getId(),
                "fullName", u.getFullName(),
                "email", u.getEmail(),
                "courseCount", courseRepository.countByTeacherId(u.getId())
        )).collect(Collectors.toList());

        List<Map<String, Object>> topStudentsList = userRepository.findTopStudents().stream().map(u -> Map.<String, Object>of(
                "id", u.getId(),
                "fullName", u.getFullName(),
                "email", u.getEmail(),
                "enrollmentCount", enrollmentRepository.countByStudentId(u.getId())
        )).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "totalStudents", totalStudents,
                "totalTeachers", totalTeachers,
                "totalCourses", totalCourses,
                "totalEnrollments", totalEnrollments,
                "topTeachers", topTeachersList,
                "topStudents", topStudentsList
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

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Map<String, String>> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newRole = body.get("role");
        if (newRole == null || newRole.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le rôle est requis"));
        }
        User user = userService.getUserById(id);
        user.setRole(Role.valueOf(newRole.toUpperCase()));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Rôle mis à jour avec succès"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ========== COURSE MANAGEMENT ==========

    @DeleteMapping("/courses/all")
    public ResponseEntity<Void> deleteAllCourses() {
        courseRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/system-reset-seed")
    public ResponseEntity<Map<String, String>> triggerSystemResetAndSeed() {
        try {
            // 1. Delete all courses and categories
            courseRepository.deleteAll();
            categoryRepository.deleteAll();

            // 2. Delete all users except Admin, Amin Frikha, Donia Bahloul
            List<User> users = userRepository.findAll();
            User amin = null;
            for (User u : users) {
                if (u.getRole() == Role.ADMIN) continue;
                String name = u.getFullName().toLowerCase();
                if (name.contains("amin frikha")) {
                    amin = u;
                    continue;
                }
                if (name.contains("donia bahloul")) {
                    continue;
                }
                userRepository.delete(u);
            }

            if (amin == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur 'Amin Frikha' introuvable dans la base !"));
            }

            if (amin.getRole() != Role.TEACHER) {
                amin.setRole(Role.TEACHER);
                userRepository.save(amin);
            }

            // 3. Create Categories
            Category catWeb = Category.builder().name("Développement Web").icon("🌐").description("Technologies du web").build();
            Category catData = Category.builder().name("Data Science").icon("📊").description("Données et intelligence artificielle").build();
            Category catDev = Category.builder().name("Développement Logiciel").icon("💻").description("Architecture et concepts de programmation").build();
            catWeb = categoryRepository.save(catWeb);
            catData = categoryRepository.save(catData);
            catDev = categoryRepository.save(catDev);

            // 4. Seed Course 1: React
            com.pfa.elearning.model.Course c1 = com.pfa.elearning.model.Course.builder()
                    .title("React.js Moderne")
                    .description("Maîtrisez React, ses hooks, et le routage avec React Router pour créer des applications web complètes.")
                    .level(com.pfa.elearning.model.DifficultyLevel.INTERMEDIATE)
                    .teacher(amin)
                    .category(catWeb)
                    .published(true)
                    .build();

            com.pfa.elearning.model.Chapter ch1 = com.pfa.elearning.model.Chapter.builder().title("Introduction et Composants").content("Un composant React est une fonction JavaScript qui retourne du JSX...").chapterOrder(1).supportType(com.pfa.elearning.model.SupportType.TEXT).course(c1).build();
            com.pfa.elearning.model.Chapter ch2 = com.pfa.elearning.model.Chapter.builder().title("Les Hooks : useState et useEffect").content("useState permet de gérer l'état local...").chapterOrder(2).supportType(com.pfa.elearning.model.SupportType.TEXT).course(c1).build();
            c1.getChapters().add(ch1);
            c1.getChapters().add(ch2);

            com.pfa.elearning.model.Quiz q1 = com.pfa.elearning.model.Quiz.builder().title("Quiz d'évaluation React").description("Testez vos connaissances en React").level(com.pfa.elearning.model.DifficultyLevel.INTERMEDIATE).published(true).teacher(amin).course(c1).build();
            q1.getQuestions().add(com.pfa.elearning.model.QuizQuestion.builder().quiz(q1).text("Que signifie JSX ?").correctAnswer(0).options(java.util.List.of("JavaScript XML", "Java Syntax Extension", "JSON X", "JavaScript Extension")).build());
            q1.getQuestions().add(com.pfa.elearning.model.QuizQuestion.builder().quiz(q1).text("Quel hook est utilisé pour les effets de bord ?").correctAnswer(1).options(java.util.List.of("useState", "useEffect", "useMemo", "useContext")).build());
            c1.getQuizzes().add(q1);

            com.pfa.elearning.model.Exercise ex1 = com.pfa.elearning.model.Exercise.builder().title("TP1: Todo List").description("Créez une Todo List simple avec React").content("Directives: 1. Utilisez useState pour la liste globale. 2. Créez un composant TodoItem.").level(com.pfa.elearning.model.DifficultyLevel.BEGINNER).published(true).teacher(amin).course(c1).build();
            c1.getExercises().add(ex1);

            courseRepository.save(c1);

            // 5. Seed Course 2: Java Spring Boot
            com.pfa.elearning.model.Course c2 = com.pfa.elearning.model.Course.builder()
                    .title("Architecture Microservices en Java")
                    .description("Développez et déployez des microservices robustes avec Spring Boot, Spring Cloud, et Docker.")
                    .level(com.pfa.elearning.model.DifficultyLevel.ADVANCED)
                    .teacher(amin)
                    .category(catDev)
                    .published(true)
                    .build();

            com.pfa.elearning.model.Chapter ch3 = com.pfa.elearning.model.Chapter.builder().title("Concepts de Base").content("Spring Boot simplifie la création d'applications Spring autonomes prêtes pour la production...").chapterOrder(1).supportType(com.pfa.elearning.model.SupportType.TEXT).course(c2).build();
            c2.getChapters().add(ch3);

            com.pfa.elearning.model.Quiz q2 = com.pfa.elearning.model.Quiz.builder().title("Quiz Java Spring Boot").description("Révision de l'architecture backend").level(com.pfa.elearning.model.DifficultyLevel.ADVANCED).published(true).teacher(amin).course(c2).build();
            q2.getQuestions().add(com.pfa.elearning.model.QuizQuestion.builder().quiz(q2).text("Quelle annotation démarre l'application ?").correctAnswer(2).options(java.util.List.of("@Service", "@Controller", "@SpringBootApplication", "@RestController")).build());
            c2.getQuizzes().add(q2);

            courseRepository.save(c2);

            return ResponseEntity.ok(Map.of("message", "Base de données purgée et pré-remplie avec succès."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erreur lors du reset: " + e.getMessage()));
        }
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
