package com.pfa.elearning.repository;

import com.pfa.elearning.model.ChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterProgressRepository extends JpaRepository<ChapterProgress, Long> {
    List<ChapterProgress> findByEnrollmentId(Long enrollmentId);
    Optional<ChapterProgress> findByEnrollmentIdAndChapterId(Long enrollmentId, Long chapterId);
}
