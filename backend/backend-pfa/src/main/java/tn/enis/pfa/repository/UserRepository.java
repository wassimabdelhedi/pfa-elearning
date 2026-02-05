package tn.enis.pfa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.enis.pfa.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
}
