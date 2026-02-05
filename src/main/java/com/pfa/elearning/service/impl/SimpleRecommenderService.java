package com.pfa.elearning.service.impl;

import com.pfa.elearning.dto.course.CourseResponse;
import com.pfa.elearning.entity.Course;
import com.pfa.elearning.entity.User;
import com.pfa.elearning.mapper.CourseMapper;
import com.pfa.elearning.repository.CourseRepository;
import com.pfa.elearning.repository.EnrollmentRepository;
import com.pfa.elearning.repository.UserRepository;
import com.pfa.elearning.service.RecommenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple implementation of RecommenderService.
 * 
 * This is a placeholder/baseline implementation that uses basic heuristics.
 * It will be replaced with AI-based algorithms in the future.
 * 
 * Current approach:
 * - Recommendations based on category matching
 * - Popularity based on enrollment count
 * - No ML/AI algorithms used
 */
@Service
@RequiredArgsConstructor
public class SimpleRecommenderService implements RecommenderService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper;

    /**
     * Simple recommendation based on user's enrolled courses' categories.
     * TODO: Replace with ML-based collaborative/content filtering
     */
    @Override
    public List<CourseResponse> getRecommendedCourses(Long userId, int limit) {
        // Get user's enrolled courses categories
        List<String> userCategories = enrollmentRepository.findByUserId(userId).stream()
                .map(e -> e.getCourse().getCategory())
                .filter(c -> c != null)
                .distinct()
                .collect(Collectors.toList());

        if (userCategories.isEmpty()) {
            // If no enrollments, return popular courses
            return getPopularCourses(limit);
        }

        // Find courses in same categories that user hasn't enrolled in
        return courseRepository.findByPublishedTrue().stream()
                .filter(c -> c.getCategory() != null && userCategories.contains(c.getCategory()))
                .filter(c -> !enrollmentRepository.existsByUserIdAndCourseId(userId, c.getId()))
                .limit(limit)
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Find similar courses based on category and difficulty.
     * TODO: Replace with content-based filtering using course features
     */
    @Override
    public List<CourseResponse> getSimilarCourses(Long courseId, int limit) {
        Course targetCourse = courseRepository.findById(courseId).orElse(null);
        if (targetCourse == null) {
            return Collections.emptyList();
        }

        return courseRepository.findByPublishedTrue().stream()
                .filter(c -> !c.getId().equals(courseId))
                .filter(c -> {
                    boolean categoryMatch = targetCourse.getCategory() != null 
                            && targetCourse.getCategory().equals(c.getCategory());
                    boolean difficultyMatch = targetCourse.getDifficulty() != null 
                            && targetCourse.getDifficulty().equals(c.getDifficulty());
                    return categoryMatch || difficultyMatch;
                })
                .limit(limit)
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get popular courses based on enrollment count.
     * TODO: Add time-decay factor and engagement metrics
     */
    @Override
    public List<CourseResponse> getPopularCourses(int limit) {
        return courseRepository.findByPublishedTrue().stream()
                .sorted(Comparator.comparingLong(c -> 
                        -enrollmentRepository.countByCourseId(c.getId())))
                .limit(limit)
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get courses matching user's learning goals.
     * TODO: Implement NLP-based goal matching
     */
    @Override
    public List<CourseResponse> getGoalBasedRecommendations(Long userId, int limit) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getGoals() == null) {
            return getPopularCourses(limit);
        }

        // Simple keyword matching (placeholder for NLP)
        String goals = user.getGoals().toLowerCase();
        
        return courseRepository.findByPublishedTrue().stream()
                .filter(c -> {
                    String desc = (c.getDescription() != null ? c.getDescription() : "").toLowerCase();
                    String title = c.getTitle().toLowerCase();
                    String category = (c.getCategory() != null ? c.getCategory() : "").toLowerCase();
                    
                    return goals.contains(category) || 
                           desc.contains(goals) || 
                           title.contains(goals);
                })
                .limit(limit)
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get next recommended content in a course.
     * TODO: Implement adaptive learning path based on user performance
     */
    @Override
    public Long getNextRecommendedContent(Long userId, Long courseId) {
        // Placeholder: This would analyze user's progress, scores, and learning patterns
        // to recommend the optimal next piece of content
        // For now, returns null (no recommendation)
        return null;
    }
}
