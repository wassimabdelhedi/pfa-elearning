package com.pfa.elearning.controller;

import com.pfa.elearning.exception.ResourceNotFoundException;
import com.pfa.elearning.exception.UnauthorizedException;
import com.pfa.elearning.model.*;
import com.pfa.elearning.repository.CategoryRepository;
import com.pfa.elearning.repository.QuizRepository;
import com.pfa.elearning.repository.QuizResultRepository;
import com.pfa.elearning.service.CourseService;
import com.pfa.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getPublishedQuizzes() {
        List<Quiz> quizzes = quizRepository.findByPublishedTrue();
        return ResponseEntity.ok(quizzes.stream().map(this::toMap).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getQuizById(@PathVariable Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        return ResponseEntity.ok(toMap(quiz));
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
        if (courseIdObj != null && !courseIdObj.toString().isEmpty()) {
            Long courseId = Long.valueOf(courseIdObj.toString());
            Course course = courseService.getCourseById(courseId);
            quiz.setCourse(course);
        }

        // Questions
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> questionsData = (List<Map<String, Object>>) body.get("questions");
        if (questionsData != null) {
            for (Map<String, Object> qData : questionsData) {
                QuizQuestion question = QuizQuestion.builder()
                        .text((String) qData.get("text"))
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

        QuizResult result = QuizResult.builder()
                .quiz(quiz)
                .student(student)
                .score(score)
                .totalQuestions(totalQuestions)
                .build();

        quizResultRepository.save(result);

        Map<String, Object> response = new HashMap<>();
        response.put("id", result.getId());
        response.put("score", result.getScore());
        response.put("totalQuestions", result.getTotalQuestions());
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
            map.put("score", r.getScore());
            map.put("totalQuestions", r.getTotalQuestions());
            map.put("percentage", r.getTotalQuestions() > 0 ? Math.round((double) r.getScore() / r.getTotalQuestions() * 100) : 0);
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
                qMap.put("options", question.getOptions());
                qMap.put("correctAnswer", question.getCorrectAnswer());
                return qMap;
            }).collect(Collectors.toList());
            map.put("questions", questionsList);
        }

        return map;
    }
}
