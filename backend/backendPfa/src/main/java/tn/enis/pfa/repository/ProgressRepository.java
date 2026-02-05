package tn.enis.pfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.pfa.entity.Progress;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {

    List<Progress> findByEnrollmentId(Long enrollmentId);

    Optional<Progress> findByEnrollmentIdAndContentId(Long enrollmentId, Long contentId);

    Optional<Progress> findByEnrollmentIdAndExerciseId(Long enrollmentId, Long exerciseId);
}
