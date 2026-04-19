package com.pfa.elearning.repository;

import com.pfa.elearning.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByQuizId(Long quizId);
    List<QuizResult> findByStudentId(Long studentId);
    List<QuizResult> findByQuizIdIn(List<Long> quizIds);
    boolean existsByStudentIdAndQuizId(Long studentId, Long quizId);
}
