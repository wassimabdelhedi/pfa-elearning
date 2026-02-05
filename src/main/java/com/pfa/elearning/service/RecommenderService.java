package com.pfa.elearning.service;

import com.pfa.elearning.dto.course.CourseResponse;

import java.util.List;

/**
 * Interface for the recommendation engine.
 * 
 * This interface defines the contract for course recommendations.
 * The architecture is prepared for future AI/ML integration.
 * 
 * Future implementations may include:
 * - Collaborative filtering
 * - Content-based filtering
 * - Hybrid recommendation algorithms
 * - Machine learning models
 * 
 * Data available for recommendations (via repositories):
 * - LearningHistory: User learning activities and patterns
 * - Score: Assessment results and proficiency levels
 * - TimeSpent: Engagement metrics per content
 * - UserPreference: Explicit user preferences
 * - Enrollment/Progress: Course completion data
 */
public interface RecommenderService {

    /**
     * Get personalized course recommendations for a user.
     *
     * @param userId The ID of the user to get recommendations for
     * @param limit Maximum number of recommendations to return
     * @return List of recommended courses
     */
    List<CourseResponse> getRecommendedCourses(Long userId, int limit);

    /**
     * Get courses similar to a given course.
     *
     * @param courseId The ID of the reference course
     * @param limit Maximum number of similar courses to return
     * @return List of similar courses
     */
    List<CourseResponse> getSimilarCourses(Long courseId, int limit);

    /**
     * Get trending/popular courses.
     *
     * @param limit Maximum number of courses to return
     * @return List of popular courses
     */
    List<CourseResponse> getPopularCourses(int limit);

    /**
     * Get courses based on user's learning goals.
     *
     * @param userId The ID of the user
     * @param limit Maximum number of courses to return
     * @return List of goal-aligned courses
     */
    List<CourseResponse> getGoalBasedRecommendations(Long userId, int limit);

    /**
     * Get next recommended content within a course.
     * Useful for adaptive learning paths.
     *
     * @param userId The ID of the user
     * @param courseId The ID of the current course
     * @return ID of the next recommended content, or null if none
     */
    Long getNextRecommendedContent(Long userId, Long courseId);
}
