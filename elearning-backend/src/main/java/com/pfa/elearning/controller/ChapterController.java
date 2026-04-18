package com.pfa.elearning.controller;

import com.pfa.elearning.exception.ResourceNotFoundException;
import com.pfa.elearning.exception.UnauthorizedException;
import com.pfa.elearning.model.*;
import com.pfa.elearning.repository.ChapterProgressRepository;
import com.pfa.elearning.repository.ChapterRepository;
import com.pfa.elearning.repository.EnrollmentRepository;
import com.pfa.elearning.service.CourseService;
import com.pfa.elearning.service.EnrollmentService;
import com.pfa.elearning.service.FileStorageService;
import com.pfa.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
@Slf4j
public class ChapterController {

    private final ChapterRepository chapterRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;
    private final FileStorageService fileStorageService;
    private final UserService userService;
    private final EnrollmentService enrollmentService;

    // ========== TEACHER: Manage Chapters ==========

    @PostMapping(value = "/course/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addChapter(
            @PathVariable Long courseId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "supportType", defaultValue = "TEXT") String supportType,
            @RequestParam(value = "supportLink", required = false) String supportLink,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Authentication authentication) {

        User teacher = userService.getUserByEmail(authentication.getName());
        Course course = courseService.getCourseById(courseId);

        if (!course.getTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedException("You can only add chapters to your own courses");
        }

        // Determine the next order
        int nextOrder = chapterRepository.findByCourseIdOrderByChapterOrderAsc(courseId).size() + 1;

        Chapter chapter = Chapter.builder()
                .title(title)
                .description(description)
                .supportType(SupportType.valueOf(supportType))
                .supportLink(supportLink)
                .content(content)
                .chapterOrder(nextOrder)
                .course(course)
                .build();

        // Handle file upload
        if (file != null && !file.isEmpty()) {
            String storedFileName = fileStorageService.storeFile(file);
            chapter.setFilePath(storedFileName);
            chapter.setOriginalFileName(file.getOriginalFilename());

            // Extract text from uploaded file for AI indexing
            String extractedText = courseService.extractTextFromFile(file);
            if (extractedText != null && !extractedText.isEmpty()) {
                chapter.setContent(extractedText);
                log.info("Extracted {} chars from chapter file: {}", extractedText.length(), file.getOriginalFilename());
            }
        }

        Chapter saved = chapterRepository.save(chapter);

        return ResponseEntity.status(HttpStatus.CREATED).body(chapterToMap(saved));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Map<String, Object>>> getChapters(@PathVariable Long courseId) {
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByChapterOrderAsc(courseId);
        return ResponseEntity.ok(chapters.stream().map(this::chapterToMap).collect(Collectors.toList()));
    }

    @PutMapping("/{chapterId}")
    public ResponseEntity<Map<String, Object>> updateChapter(
            @PathVariable Long chapterId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "supportType", required = false) String supportType,
            @RequestParam(value = "supportLink", required = false) String supportLink,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Authentication authentication) {

        User teacher = userService.getUserByEmail(authentication.getName());
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter", "id", chapterId));

        if (!chapter.getCourse().getTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedException("You can only update chapters in your own courses");
        }

        if (title != null) chapter.setTitle(title);
        if (description != null) chapter.setDescription(description);
        if (supportType != null) chapter.setSupportType(SupportType.valueOf(supportType));
        if (supportLink != null) chapter.setSupportLink(supportLink);
        if (content != null) chapter.setContent(content);

        if (file != null && !file.isEmpty()) {
            // Delete old file
            if (chapter.getFilePath() != null) {
                fileStorageService.deleteFile(chapter.getFilePath());
            }
            String storedFileName = fileStorageService.storeFile(file);
            chapter.setFilePath(storedFileName);
            chapter.setOriginalFileName(file.getOriginalFilename());

            String extractedText = courseService.extractTextFromFile(file);
            if (extractedText != null && !extractedText.isEmpty()) {
                chapter.setContent(extractedText);
            }
        }

        Chapter saved = chapterRepository.save(chapter);
        return ResponseEntity.ok(chapterToMap(saved));
    }

    @DeleteMapping("/{chapterId}")
    public ResponseEntity<Void> deleteChapter(
            @PathVariable Long chapterId,
            Authentication authentication) {

        User teacher = userService.getUserByEmail(authentication.getName());
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter", "id", chapterId));

        if (!chapter.getCourse().getTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedException("You can only delete chapters in your own courses");
        }

        if (chapter.getFilePath() != null) {
            fileStorageService.deleteFile(chapter.getFilePath());
        }

        chapterRepository.delete(chapter);
        return ResponseEntity.noContent().build();
    }

    // ========== STUDENT: Chapter File Access ==========

    @GetMapping("/{chapterId}/download")
    public ResponseEntity<Resource> downloadChapterFile(@PathVariable Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter", "id", chapterId));

        if (chapter.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = fileStorageService.getFilePath(chapter.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String fileName = chapter.getOriginalFileName() != null
                        ? chapter.getOriginalFileName()
                        : chapter.getFilePath();

                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                        .header("Access-Control-Expose-Headers", "Content-Disposition")
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error downloading file for chapter {}: {}", chapterId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{chapterId}/stream")
    public ResponseEntity<Resource> streamChapterVideo(@PathVariable Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter", "id", chapterId));

        if (chapter.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = fileStorageService.getFilePath(chapter.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String fileName = chapter.getFilePath().toLowerCase();
                String contentType = "application/octet-stream";
                if (fileName.endsWith(".mp4")) contentType = "video/mp4";
                else if (fileName.endsWith(".webm")) contentType = "video/webm";
                else if (fileName.endsWith(".avi")) contentType = "video/x-msvideo";
                else if (fileName.endsWith(".mov")) contentType = "video/quicktime";
                else if (fileName.endsWith(".mkv")) contentType = "video/x-matroska";

                return ResponseEntity.ok()
                        .header("Content-Type", contentType)
                        .header("Accept-Ranges", "bytes")
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error streaming video for chapter {}: {}", chapterId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== STUDENT: Chapter Progress ==========

    @PutMapping("/{chapterId}/complete")
    public ResponseEntity<Map<String, Object>> markChapterComplete(
            @PathVariable Long chapterId,
            Authentication authentication) {

        User student = userService.getUserByEmail(authentication.getName());
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter", "id", chapterId));

        Long courseId = chapter.getCourse().getId();

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "courseId", courseId));

        // Find or create chapter progress
        ChapterProgress progress = chapterProgressRepository
                .findByEnrollmentIdAndChapterId(enrollment.getId(), chapterId)
                .orElseGet(() -> ChapterProgress.builder()
                        .enrollment(enrollment)
                        .chapter(chapter)
                        .build());

        if (!progress.isCompleted()) {
            progress.setCompleted(true);
            progress.setProgressPercentage(100.0);
            progress.setCompletedAt(LocalDateTime.now());
            chapterProgressRepository.save(progress);
        }

        // Recalculate overall course progress using centralized service
        Enrollment updatedEnrollment = enrollmentService.updateEnrollmentStatus(enrollment.getId());

        List<Chapter> allChapters = chapterRepository.findByCourseIdOrderByChapterOrderAsc(courseId);
        List<ChapterProgress> allProgresses = chapterProgressRepository.findByEnrollmentId(updatedEnrollment.getId());
        long completedChapters = allProgresses.stream().filter(ChapterProgress::isCompleted).count();
        boolean chaptersCompleted = enrollmentService.isChaptersFinished(updatedEnrollment);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("chapterId", chapterId);
        result.put("chapterCompleted", true);
        result.put("chaptersCompleted", chaptersCompleted);
        result.put("courseProgress", Math.round(updatedEnrollment.getProgressPercentage()));
        result.put("courseCompleted", updatedEnrollment.isCompleted());
        result.put("completedChapters", completedChapters);
        result.put("totalChapters", allChapters.size());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/course/{courseId}/progress")
    public ResponseEntity<Map<String, Object>> getCourseChapterProgress(
            @PathVariable Long courseId,
            Authentication authentication) {

        User student = userService.getUserByEmail(authentication.getName());

        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId);
        if (enrollmentOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("enrolled", false));
        }

        Enrollment enrollment = enrollmentOpt.get();
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByChapterOrderAsc(courseId);
        List<ChapterProgress> progresses = chapterProgressRepository.findByEnrollmentId(enrollment.getId());

        Set<Long> completedChapterIds = progresses.stream()
                .filter(ChapterProgress::isCompleted)
                .map(p -> p.getChapter().getId())
                .collect(Collectors.toSet());

        List<Map<String, Object>> chapterList = chapters.stream().map(ch -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("chapterId", ch.getId());
            map.put("title", ch.getTitle());
            map.put("order", ch.getChapterOrder());
            map.put("completed", completedChapterIds.contains(ch.getId()));
            return map;
        }).collect(Collectors.toList());

        boolean chaptersCompleted = enrollmentService.isChaptersFinished(enrollment);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("enrolled", true);
        result.put("courseCompleted", enrollment.isCompleted());
        result.put("chaptersCompleted", chaptersCompleted);
        result.put("overallProgress", Math.round(enrollment.getProgressPercentage()));
        result.put("completedChapters", completedChapterIds.size());
        result.put("totalChapters", chapters.size());
        result.put("chapters", chapterList);

        return ResponseEntity.ok(result);
    }

    // ========== Helper ==========

    private Map<String, Object> chapterToMap(Chapter ch) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", ch.getId());
        map.put("title", ch.getTitle());
        map.put("description", ch.getDescription());
        map.put("content", ch.getContent());
        map.put("supportType", ch.getSupportType() != null ? ch.getSupportType().name() : null);
        map.put("filePath", ch.getFilePath());
        map.put("originalFileName", ch.getOriginalFileName());
        map.put("supportLink", ch.getSupportLink());
        map.put("chapterOrder", ch.getChapterOrder());
        map.put("courseId", ch.getCourse().getId());
        return map;
    }
}
