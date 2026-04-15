package com.pfa.elearning.repository;

import com.pfa.elearning.model.User;
import com.pfa.elearning.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByActiveTrue();

    @org.springframework.data.jpa.repository.Query(value = "SELECT u.* FROM users u LEFT JOIN courses c ON u.id = c.teacher_id WHERE u.role = 'TEACHER' GROUP BY u.id ORDER BY count(c.id) DESC LIMIT 5", nativeQuery = true)
    List<User> findTopTeachers();

    @org.springframework.data.jpa.repository.Query(value = "SELECT u.* FROM users u LEFT JOIN enrollments e ON u.id = e.student_id WHERE u.role = 'STUDENT' GROUP BY u.id ORDER BY count(e.id) DESC LIMIT 5", nativeQuery = true)
    List<User> findTopStudents();
}
