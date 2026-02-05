package tn.enis.pfa.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.pfa.entity.Progress;
import tn.enis.pfa.entity.User;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByStudent(User student);
}
