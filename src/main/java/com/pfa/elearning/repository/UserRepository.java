package com.pfa.elearning.repository;

import com.pfa.elearning.entity.User;
import com.pfa.elearning.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByEnabledTrue();

    List<User> findByRoleAndEnabledTrue(Role role);
}
