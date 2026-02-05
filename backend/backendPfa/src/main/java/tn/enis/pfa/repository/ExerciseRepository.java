package tn.enis.pfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.pfa.entity.Exercise;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByModuleIdOrderByOrderIndexAsc(Long moduleId);
}
