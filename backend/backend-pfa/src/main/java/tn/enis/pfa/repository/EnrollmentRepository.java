package tn.enis.pfa.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.pfa.entity.Enrollment;
import tn.enis.pfa.entity.User;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(User student);
}
