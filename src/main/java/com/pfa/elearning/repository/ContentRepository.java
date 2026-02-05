package com.pfa.elearning.repository;

import com.pfa.elearning.entity.Content;
import com.pfa.elearning.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Content entity operations.
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByChapterIdOrderByOrderIndexAsc(Long chapterId);

    List<Content> findByChapterIdAndContentType(Long chapterId, ContentType contentType);

    Optional<Content> findByChapterIdAndOrderIndex(Long chapterId, Integer orderIndex);

    @Query("SELECT MAX(c.orderIndex) FROM Content c WHERE c.chapter.id = :chapterId")
    Optional<Integer> findMaxOrderIndexByChapterId(@Param("chapterId") Long chapterId);

    @Query("SELECT COUNT(c) FROM Content c WHERE c.chapter.id = :chapterId")
    Long countByChapterId(@Param("chapterId") Long chapterId);

    @Query("SELECT SUM(c.durationMinutes) FROM Content c WHERE c.chapter.id = :chapterId")
    Optional<Integer> getTotalDurationByChapterId(@Param("chapterId") Long chapterId);
}
