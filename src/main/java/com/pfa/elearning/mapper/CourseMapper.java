package com.pfa.elearning.mapper;

import com.pfa.elearning.dto.course.CourseResponse;
import com.pfa.elearning.entity.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for Course entity to DTO conversions.
 */
@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final ChapterMapper chapterMapper;

    public CourseResponse toResponse(Course course) {
        if (course == null) return null;

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .difficulty(course.getDifficulty())
                .thumbnailUrl(course.getThumbnailUrl())
                .published(course.getPublished())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .teacherId(course.getTeacher() != null ? course.getTeacher().getId() : null)
                .teacherName(course.getTeacher() != null 
                        ? course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName() 
                        : null)
                .totalChapters(course.getChapters() != null ? course.getChapters().size() : 0)
                .totalContents(calculateTotalContents(course))
                .enrollmentCount(course.getEnrollments() != null ? (long) course.getEnrollments().size() : 0L)
                .build();
    }

    public CourseResponse toDetailedResponse(Course course) {
        if (course == null) return null;

        CourseResponse response = toResponse(course);
        if (course.getChapters() != null) {
            response.setChapters(course.getChapters().stream()
                    .map(chapterMapper::toDetailedResponse)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    private int calculateTotalContents(Course course) {
        if (course.getChapters() == null) return 0;
        return course.getChapters().stream()
                .mapToInt(chapter -> chapter.getContents() != null ? chapter.getContents().size() : 0)
                .sum();
    }
}
