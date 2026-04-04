package com.pfa.elearning.repository;

import com.pfa.elearning.model.ExerciseCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseCompletionRepository extends JpaRepository<ExerciseCompletion, Long> {
    List<ExerciseCompletion> findByStudentId(Long studentId);
    boolean existsByStudentIdAndExerciseId(Long studentId, Long exerciseId);
}
