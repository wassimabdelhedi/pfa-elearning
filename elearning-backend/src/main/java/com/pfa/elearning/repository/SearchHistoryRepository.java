package com.pfa.elearning.repository;

import com.pfa.elearning.model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByStudentIdOrderBySearchedAtDesc(Long studentId);
    List<SearchHistory> findTop10ByStudentIdOrderBySearchedAtDesc(Long studentId);
}
