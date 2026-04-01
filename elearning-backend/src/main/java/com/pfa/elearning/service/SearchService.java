package com.pfa.elearning.service;

import com.pfa.elearning.dto.response.RecommendationResponse;
import com.pfa.elearning.dto.response.SearchResponse;
import com.pfa.elearning.model.*;
import com.pfa.elearning.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final CourseRepository courseRepository;
    private final ExerciseRepository exerciseRepository;
    private final QuizRepository quizRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final RecommendationRepository recommendationRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRatingRepository courseRatingRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${app.ai-service.base-url}")
    private String aiServiceBaseUrl;

    @Transactional
    public SearchResponse search(String query, User student) {
        log.info("Student {} searching for: {}", student.getEmail(), query);

        // 1. Get all published courses for the AI to compare against
        List<Course> allCourses = courseRepository.findByPublishedTrue();

        // 2. Build student profile for personalized recommendations
        List<Long> enrolledCourseIds = enrollmentRepository.findByStudentId(student.getId())
                .stream().map(e -> e.getCourse().getId()).collect(Collectors.toList());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("student_id", student.getId());
        requestBody.put("enrolled_courses", enrolledCourseIds);
        requestBody.put("courses", allCourses.stream().map(c -> {
            Map<String, Object> courseMap = new HashMap<>();
            courseMap.put("id", c.getId());
            courseMap.put("title", c.getTitle());
            courseMap.put("description", c.getDescription() != null ? c.getDescription() : "");
            courseMap.put("content", c.getContent() != null ? c.getContent() : "");
            courseMap.put("level", c.getLevel() != null ? c.getLevel().name() : "BEGINNER");
            courseMap.put("category", c.getCategory() != null ? c.getCategory().getName() : "");
            return courseMap;
        }).collect(Collectors.toList()));

        // 3. Call AI recommendation service
        List<Map<String, Object>> aiResults;
        try {
            aiResults = webClientBuilder.build()
                    .post()
                    .uri(aiServiceBaseUrl + "/api/recommend")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .map(response -> {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> recs = (List<Map<String, Object>>) response.get("recommendations");
                        return recs != null ? recs : new ArrayList<Map<String, Object>>();
                    })
                    .block();
        } catch (Exception e) {
            log.warn("AI service unavailable, falling back to keyword search: {}", e.getMessage());
            aiResults = null;
        }

        // 4. Build recommendations
        List<RecommendationResponse> recommendations;
        List<String> extractedKeywords = new ArrayList<>();

        if (aiResults != null && !aiResults.isEmpty()) {
            // Use AI results
            recommendations = aiResults.stream().map(result -> {
                Long courseId = ((Number) result.get("course_id")).longValue();
                double score = ((Number) result.get("score")).doubleValue();
                String reason = (String) result.getOrDefault("reason", "AI recommendation");

                Course course = courseRepository.findById(courseId).orElse(null);
                if (course == null) return null;

                Double avgRating = courseRatingRepository.getAverageRatingByCourseId(courseId);

                return RecommendationResponse.builder()
                        .courseId(courseId)
                        .courseTitle(course.getTitle())
                        .courseDescription(course.getDescription())
                        .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                        .teacherName(course.getTeacher().getFullName())
                        .level(course.getLevel() != null ? course.getLevel().name() : null)
                        .relevanceScore(score)
                        .reason(reason)
                        .averageRating(avgRating != null ? avgRating : 0.0)
                        .build();
            }).filter(Objects::nonNull).collect(Collectors.toList());

            // Extract keywords from AI response if available
            // Keywords extraction happens on AI side
            extractedKeywords = List.of(query.split("\\s+"));
        } else {
            // Fallback: simple keyword search
            List<Course> foundCourses = courseRepository.searchByKeyword(query);
            recommendations = foundCourses.stream().map(course -> {
                Double avgRating = courseRatingRepository.getAverageRatingByCourseId(course.getId());
                return RecommendationResponse.builder()
                        .courseId(course.getId())
                        .courseTitle(course.getTitle())
                        .courseDescription(course.getDescription())
                        .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                        .teacherName(course.getTeacher().getFullName())
                        .level(course.getLevel() != null ? course.getLevel().name() : null)
                        .relevanceScore(0.5)
                        .reason("Keyword match: " + query)
                        .averageRating(avgRating != null ? avgRating : 0.0)
                        .build();
            }).collect(Collectors.toList());
            extractedKeywords = List.of(query.split("\\s+"));
        }

        // 5. Search exercises and quizzes by keyword
        List<Map<String, Object>> exerciseResults = new ArrayList<>();
        List<Map<String, Object>> quizResults = new ArrayList<>();

        // Search each word in the query for exercises and quizzes
        Set<Long> seenExerciseIds = new HashSet<>();
        Set<Long> seenQuizIds = new HashSet<>();
        String[] queryWords = query.split("\\s+");

        for (String word : queryWords) {
            if (word.length() < 3) continue;

            // Search exercises
            List<Exercise> matchedExercises = exerciseRepository.searchByKeyword(word);
            for (Exercise ex : matchedExercises) {
                if (seenExerciseIds.add(ex.getId())) {
                    Map<String, Object> exMap = new HashMap<>();
                    exMap.put("id", ex.getId());
                    exMap.put("title", ex.getTitle());
                    exMap.put("description", ex.getDescription());
                    exMap.put("level", ex.getLevel() != null ? ex.getLevel().name() : null);
                    exMap.put("categoryName", ex.getCategory() != null ? ex.getCategory().getName() : null);
                    exMap.put("teacherName", ex.getTeacher().getFullName());
                    exMap.put("courseName", ex.getCourse() != null ? ex.getCourse().getTitle() : null);
                    exMap.put("filePath", ex.getFilePath());
                    exMap.put("originalFileName", ex.getOriginalFileName());
                    exerciseResults.add(exMap);
                }
            }

            // Search quizzes
            List<Quiz> matchedQuizzes = quizRepository.searchByKeyword(word);
            for (Quiz qz : matchedQuizzes) {
                if (seenQuizIds.add(qz.getId())) {
                    Map<String, Object> qzMap = new HashMap<>();
                    qzMap.put("id", qz.getId());
                    qzMap.put("title", qz.getTitle());
                    qzMap.put("description", qz.getDescription());
                    qzMap.put("level", qz.getLevel() != null ? qz.getLevel().name() : null);
                    qzMap.put("categoryName", qz.getCategory() != null ? qz.getCategory().getName() : null);
                    qzMap.put("teacherName", qz.getTeacher().getFullName());
                    qzMap.put("courseName", qz.getCourse() != null ? qz.getCourse().getTitle() : null);
                    qzMap.put("questionsCount", qz.getQuestions() != null ? qz.getQuestions().size() : 0);
                    quizResults.add(qzMap);
                }
            }
        }

        int totalResults = recommendations.size() + exerciseResults.size() + quizResults.size();

        // 6. Save search history
        SearchHistory history = SearchHistory.builder()
                .student(student)
                .query(query)
                .extractedKeywords(String.join(", ", extractedKeywords))
                .resultsCount(totalResults)
                .build();
        searchHistoryRepository.save(history);

        // 7. Save recommendations to DB
        for (RecommendationResponse rec : recommendations) {
            Course course = courseRepository.findById(rec.getCourseId()).orElse(null);
            if (course != null) {
                Recommendation recommendation = Recommendation.builder()
                        .student(student)
                        .course(course)
                        .relevanceScore(rec.getRelevanceScore())
                        .reason(rec.getReason())
                        .build();
                recommendationRepository.save(recommendation);
            }
        }

        return SearchResponse.builder()
                .query(query)
                .extractedKeywords(extractedKeywords)
                .recommendations(recommendations)
                .exercises(exerciseResults)
                .quizzes(quizResults)
                .totalResults(totalResults)
                .build();
    }

    public void indexCourse(Course course) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", course.getId());
            requestBody.put("title", course.getTitle());
            requestBody.put("description", course.getDescription() != null ? course.getDescription() : "");
            requestBody.put("content", course.getContent() != null ? course.getContent() : "");
            requestBody.put("category", course.getCategory() != null ? course.getCategory().getName() : "");

            Map<String, Object> response = webClientBuilder.build()
                    .post()
                    .uri(aiServiceBaseUrl + "/api/index-course")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response != null && response.containsKey("keywords")) {
                String keywords = (String) response.get("keywords");
                course.setKeywords(keywords);
                courseRepository.save(course);
                log.info("Course {} indexed successfully with keywords: {}", course.getId(), keywords);
            }
        } catch (Exception e) {
            log.warn("Could not index course {} in AI service: {}", course.getId(), e.getMessage());
        }
    }
}
