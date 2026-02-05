package tn.enis.pfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.pfa.entity.Course;
import tn.enis.pfa.entity.Enrollment;
import tn.enis.pfa.entity.User;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByUserAndCourse(User user, Course course);

    List<Enrollment> findByUser(User user);

    boolean existsByUserAndCourse(User user, Course course);
}
