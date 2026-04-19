package com.pfa.elearning.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfa.elearning.exception.ForbiddenException;
import com.pfa.elearning.exception.ResourceNotFoundException;
import com.pfa.elearning.exception.UnauthorizedException;
import com.pfa.elearning.model.*;
import com.pfa.elearning.repository.CategoryRepository;
import com.pfa.elearning.repository.QuizRepository;
import com.pfa.elearning.repository.QuizResultRepository;
import com.pfa.elearning.repository.EnrollmentRepository;
import com.pfa.elearning.service.CourseService;
import com.pfa.elearning.service.EnrollmentService;
import com.pfa.elearning.service.SearchService;
import com.pfa.elearning.service.UserService;
import com.pfa.elearning.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizRepository quizRepository;
    private final QuizResultRepository quizResultRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final CourseService courseService;
    private final SearchService searchService;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentService enrollmentService;
    private final EmailService emailService;

    @Value("${app.ai-service.base-url}")
    private String aiServiceBaseUrl;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getPublishedQuizzes() {
        List<Quiz> quizzes = quizRepository.findByPublishedTrue();
        return ResponseEntity.ok(quizzes.stream().map(this::toMap).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getQuizById(@PathVariable Long id, Authentication authentication) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        // Security check for students
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            User student = userService.getUserByEmail(authentication.getName());
            if (quiz.getCourse() != null) {
                Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), quiz.getCourse().getId());
                if (enrollmentOpt.isEmpty() || !enrollmentService.isChaptersFinished(enrollmentOpt.get())) {
                    throw new ForbiddenException("Terminez tous les chapitres pour débloquer les quiz");
                }
            } else {
                // Quizzes not attached to a course are not accessible to students in this strict mode
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return ResponseEntity.ok(toMap(quiz));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Map<String, Object>>> getQuizzesByCourse(@PathVariable Long courseId, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            User student = userService.getUserByEmail(authentication.getName());
            Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId);
            if (enrollmentOpt.isEmpty() || !enrollmentService.isChaptersFinished(enrollmentOpt.get())) {
                throw new ForbiddenException("Terminez tous les chapitres pour débloquer les quiz");
            }
        }

        List<Quiz> quizzes = quizRepository.findByCourseId(courseId)
                                           .stream()
                                           .filter(Quiz::isPublished)
                                           .collect(Collectors.toList());
        return ResponseEntity.ok(quizzes.stream().map(this::toMap).collect(Collectors.toList()));
    }

    @GetMapping("/my-quizzes")
    public ResponseEntity<List<Map<String, Object>>> getMyQuizzes(Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        List<Quiz> quizzes = quizRepository.findByTeacherId(teacher.getId());
        return ResponseEntity.ok(quizzes.stream().map(this::toMap).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createQuiz(
            @RequestBody Map<String, Object> body,
            Authentication authentication) {

        User teacher = userService.getUserByEmail(authentication.getName());

        String title = (String) body.get("title");
        String description = (String) body.get("description");
        String levelStr = (String) body.getOrDefault("level", "BEGINNER");
        boolean published = Boolean.TRUE.equals(body.get("published"));

        Quiz quiz = Quiz.builder()
                .title(title)
                .description(description)
                .level(DifficultyLevel.valueOf(levelStr))
                .published(published)
                .teacher(teacher)
                .build();

        // Category
        Object categoryIdObj = body.get("categoryId");
        if (categoryIdObj != null && !categoryIdObj.toString().isEmpty()) {
            Long categoryId = Long.valueOf(categoryIdObj.toString());
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
            quiz.setCategory(category);
        }

        // Course
        Object courseIdObj = body.get("courseId");
        if (courseIdObj == null || courseIdObj.toString().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le cours est obligatoire pour créer un quiz"));
        }
        Long courseId = Long.valueOf(courseIdObj.toString());
        Course course = courseService.getCourseById(courseId);
        quiz.setCourse(course);

        // Questions
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> questionsData = (List<Map<String, Object>>) body.get("questions");
        if (questionsData != null) {
            for (Map<String, Object> qData : questionsData) {
                QuizQuestion question = QuizQuestion.builder()
                        .text((String) qData.get("text"))
                        .topic((String) qData.getOrDefault("topic", "General"))
                        .correctAnswer(((Number) qData.get("correctAnswer")).intValue())
                        .quiz(quiz)
                        .build();

                @SuppressWarnings("unchecked")
                List<String> options = (List<String>) qData.get("options");
                if (options != null) {
                    question.setOptions(new ArrayList<>(options));
                }

                quiz.getQuestions().add(question);
            }
        }

        quiz = quizRepository.save(quiz);
        return ResponseEntity.status(HttpStatus.CREATED).body(toMap(quiz));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id, Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        if (!quiz.getTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedException("You can only delete your own quizzes");
        }

        quizRepository.delete(quiz);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-publish")
    public ResponseEntity<Map<String, Object>> togglePublishQuiz(
            @PathVariable Long id,
            Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        if (!quiz.getTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedException("You can only modify your own quizzes");
        }

        quiz.setPublished(!quiz.isPublished());
        quiz = quizRepository.save(quiz);
        return ResponseEntity.ok(toMap(quiz));
    }

    // ===== QUIZ SUBMIT (Student) =====
    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submitQuiz(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            Authentication authentication) {

        User student = userService.getUserByEmail(authentication.getName());
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        int score = ((Number) body.get("score")).intValue();
        int totalQuestions = ((Number) body.get("totalQuestions")).intValue();
        double percentage = totalQuestions > 0 ? (double) score / totalQuestions * 100 : 0;

        if (quizResultRepository.existsByStudentIdAndQuizId(student.getId(), quiz.getId())) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Vous avez déjà passé ce quiz. Un seul essai est autorisé."));
        }

        QuizResult result = QuizResult.builder()
                .quiz(quiz)
                .student(student)
                .score(score)
                .totalQuestions(totalQuestions)
                .failed(percentage < 60)
                .build();

        // Detect weak topics if failed and answers are provided
        if (percentage < 60 && body.containsKey("answers")) {
            try {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> studentAnswers = (List<Map<String, Object>>) body.get("answers");
                List<Map<String, Object>> aiQuestionsPayload = new ArrayList<>();

                for (Map<String, Object> ans : studentAnswers) {
                    Long qId = ((Number) ans.get("questionId")).longValue();
                    int studentAnsIdx = ((Number) ans.get("studentAnswerIndex")).intValue();

                    quiz.getQuestions().stream()
                            .filter(q -> q.getId().equals(qId))
                            .findFirst()
                            .ifPresent(question -> {
                                Map<String, Object> qPayload = new HashMap<>();
                                qPayload.put("text", question.getText());
                                qPayload.put("topic", question.getTopic() != null ? question.getTopic() : "General Topics");
                                
                                List<String> options = question.getOptions();
                                String studentAnsText = (studentAnsIdx >= 0 && studentAnsIdx < options.size()) ? options.get(studentAnsIdx) : "Unknown";
                                String correctAnsText = (question.getCorrectAnswer() >= 0 && question.getCorrectAnswer() < options.size()) ? options.get(question.getCorrectAnswer()) : "Unknown";
                                
                                qPayload.put("student_answer", studentAnsText);
                                qPayload.put("correct_answer", correctAnsText);
                                aiQuestionsPayload.add(qPayload);
                            });
                }

                if (!aiQuestionsPayload.isEmpty()) {
                    // Method 1: Get chapters for automated topic mapping
                    List<Map<String, Object>> aiChaptersPayload = new ArrayList<>();
                    if (quiz.getCourse() != null && quiz.getCourse().getChapters() != null) {
                        for (Chapter chapter : quiz.getCourse().getChapters()) {
                            Map<String, Object> cMap = new HashMap<>();
                            cMap.put("id", chapter.getId());
                            cMap.put("title", chapter.getTitle());
                            cMap.put("content", chapter.getContent() != null ? chapter.getContent() : "");
                            aiChaptersPayload.add(cMap);
                        }
                    }

                    Map<String, Object> finalPayload = new HashMap<>();
                    finalPayload.put("questions", aiQuestionsPayload);
                    finalPayload.put("chapters", aiChaptersPayload);

                    log.info("Sending {} questions and {} chapters to AI detection for student {}", 
                             aiQuestionsPayload.size(), aiChaptersPayload.size(), student.getId());

                    Map<String, Object> aiResponse = webClientBuilder.build()
                            .post()
                            .uri(aiServiceBaseUrl + "/api/detect-weak-topics")
                            .bodyValue(finalPayload)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                            .block();

                    if (aiResponse != null) {
                        result.setWeakTopics(objectMapper.writeValueAsString(aiResponse.get("weak_topics")));
                        
                        // Generate specialized recommendations based on these weak topics
                        try {
                            @SuppressWarnings("unchecked")
                            List<Map<String, String>> topicsList = (List<Map<String, String>>) aiResponse.get("weak_topics");
                            if (topicsList != null && !topicsList.isEmpty()) {
                                List<String> topicNames = topicsList.stream()
                                        .map(m -> m.get("topic"))
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList());
                                searchService.generateRecommendationsForWeakTopics(student, topicNames);
                            }
                        } catch (Exception e) {
                            log.error("Could not trigger specialized recommendations: {}", e.getMessage());
                        }
                        
                        // --- Generate AI Tutor Feedback in BATCH (Replaces individual calls) ---
                        List<Map<String, String>> batchQuestions = new ArrayList<>();
                        for (Map<String, Object> q : aiQuestionsPayload) {
                            String stuAns = (String) q.get("student_answer");
                            String corAns = (String) q.get("correct_answer");
                            
                            if (!stuAns.equals(corAns) && !stuAns.equals("Unknown")) {
                                Map<String, String> tutorReq = new HashMap<>();
                                tutorReq.put("question_text", (String) q.get("text"));
                                tutorReq.put("student_answer", stuAns);
                                tutorReq.put("correct_answer", corAns);
                                batchQuestions.add(tutorReq);
                            }
                        }

                        if (!batchQuestions.isEmpty()) {
                            try {
                                Map<String, Object> batchReq = new HashMap<>();
                                batchReq.put("questions", batchQuestions);

                                Map<String, Object> batchRes = webClientBuilder.build()
                                    .post()
                                    .uri(aiServiceBaseUrl + "/api/batch-tutor-feedback")
                                    .bodyValue(batchReq)
                                    .retrieve()
                                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                    .block();
                                    
                                if (batchRes != null && batchRes.containsKey("feedbacks")) {
                                    @SuppressWarnings("unchecked")
                                    List<Map<String, Object>> tutorFeedbacks = (List<Map<String, Object>>) batchRes.get("feedbacks");
                                    result.setRecommendedLearningPath(objectMapper.writeValueAsString(tutorFeedbacks));
                                }
                            } catch (Exception e) {
                                log.error("Batch AI Tutor feedback error: {}", e.getMessage());
                            }
                        }
                        // ---------------------------------------------------------------------
                    }
                }
            } catch (Exception e) {
                log.error("Failed to detect weak topics via AI: {}", e.getMessage());
            }
        }

        quizResultRepository.save(result);

        // Update enrollment status/progress
        if (quiz.getCourse() != null) {
            enrollmentRepository.findByStudentIdAndCourseId(student.getId(), quiz.getCourse().getId())
                    .ifPresent(enrollment -> enrollmentService.updateEnrollmentStatus(enrollment.getId()));
        }

        // Send email if perfect score
        if (percentage >= 100) {
            emailService.sendPerfectQuizScoreEmail(quiz.getTeacher(), student, quiz);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", result.getId());
        response.put("score", result.getScore());
        response.put("totalQuestions", result.getTotalQuestions());
        response.put("percentage", Math.round(percentage));
        response.put("failed", result.isFailed());
        response.put("weakTopics", result.getWeakTopics());
        response.put("recommendedLearningPath", result.getRecommendedLearningPath());
        response.put("studentName", student.getFullName());
        response.put("quizTitle", quiz.getTitle());
        response.put("submittedAt", result.getSubmittedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===== QUIZ RESULTS (Teacher sees students' results) =====
    @GetMapping("/{id}/results")
    public ResponseEntity<List<Map<String, Object>>> getQuizResults(
            @PathVariable Long id,
            Authentication authentication) {

        User teacher = userService.getUserByEmail(authentication.getName());
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        // Verify teacher owns this quiz
        if (!quiz.getTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedException("You can only view results of your own quizzes");
        }

        List<QuizResult> results = quizResultRepository.findByQuizId(id);
        List<Map<String, Object>> resultsList = results.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("studentName", r.getStudent().getFullName());
            map.put("studentEmail", r.getStudent().getEmail());
            map.put("score", r.getScore());
            map.put("totalQuestions", r.getTotalQuestions());
            map.put("percentage", r.getTotalQuestions() > 0 ? Math.round((double) r.getScore() / r.getTotalQuestions() * 100) : 0);
            map.put("failed", r.isFailed());
            map.put("weakTopics", r.getWeakTopics());
            map.put("recommendedLearningPath", r.getRecommendedLearningPath());
            map.put("submittedAt", r.getSubmittedAt());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultsList);
    }

    // ===== ALL RESULTS FOR TEACHER'S QUIZZES =====
    @GetMapping("/my-results")
    public ResponseEntity<List<Map<String, Object>>> getMyQuizResults(Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        List<Quiz> myQuizzes = quizRepository.findByTeacherId(teacher.getId());
        List<Long> quizIds = myQuizzes.stream().map(Quiz::getId).collect(Collectors.toList());

        if (quizIds.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<QuizResult> allResults = quizResultRepository.findByQuizIdIn(quizIds);
        List<Map<String, Object>> resultsList = allResults.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("quizId", r.getQuiz().getId());
            map.put("quizTitle", r.getQuiz().getTitle());
            map.put("studentName", r.getStudent().getFullName());
            map.put("studentEmail", r.getStudent().getEmail());
            map.put("score", r.getScore());
            map.put("totalQuestions", r.getTotalQuestions());
            map.put("percentage", r.getTotalQuestions() > 0 ? Math.round((double) r.getScore() / r.getTotalQuestions() * 100) : 0);
            map.put("failed", r.isFailed());
            map.put("weakTopics", r.getWeakTopics());
            map.put("recommendedLearningPath", r.getRecommendedLearningPath());
            map.put("submittedAt", r.getSubmittedAt());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultsList);
    }

    // ===== ALL RESULTS FOR THE STUDENT =====
    @GetMapping("/my-student-results")
    public ResponseEntity<List<Map<String, Object>>> getMyStudentResults(Authentication authentication) {
        User student = userService.getUserByEmail(authentication.getName());
        List<QuizResult> myResults = quizResultRepository.findByStudentId(student.getId());

        List<Map<String, Object>> resultsList = myResults.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("quizId", r.getQuiz().getId());
            map.put("quizTitle", r.getQuiz().getTitle());
            map.put("courseTitle", r.getQuiz().getCourse() != null ? r.getQuiz().getCourse().getTitle() : "N/A");
            map.put("score", r.getScore());
            map.put("totalQuestions", r.getTotalQuestions());
            map.put("percentage", r.getTotalQuestions() > 0 ? Math.round((double) r.getScore() / r.getTotalQuestions() * 100) : 0);
            map.put("failed", r.isFailed());
            map.put("weakTopics", r.getWeakTopics());
            map.put("recommendedLearningPath", r.getRecommendedLearningPath());
            map.put("submittedAt", r.getSubmittedAt());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultsList);
    }

    private Map<String, Object> toMap(Quiz q) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", q.getId());
        map.put("title", q.getTitle());
        map.put("description", q.getDescription());
        map.put("level", q.getLevel());
        map.put("categoryName", q.getCategory() != null ? q.getCategory().getName() : null);
        map.put("categoryId", q.getCategory() != null ? q.getCategory().getId() : null);
        map.put("teacherName", q.getTeacher().getFullName());
        map.put("teacherId", q.getTeacher().getId());
        map.put("courseName", q.getCourse() != null ? q.getCourse().getTitle() : null);
        map.put("courseId", q.getCourse() != null ? q.getCourse().getId() : null);
        map.put("published", q.isPublished());
        map.put("questionsCount", q.getQuestions() != null ? q.getQuestions().size() : 0);
        map.put("createdAt", q.getCreatedAt());

        // Include questions with options
        if (q.getQuestions() != null) {
            List<Map<String, Object>> questionsList = q.getQuestions().stream().map(question -> {
                Map<String, Object> qMap = new HashMap<>();
                qMap.put("id", question.getId());
                qMap.put("text", question.getText());
                qMap.put("topic", question.getTopic());
                qMap.put("options", question.getOptions());
                qMap.put("correctAnswer", question.getCorrectAnswer());
                return qMap;
            }).collect(Collectors.toList());
            map.put("questions", questionsList);
        }

        return map;
    }
}
