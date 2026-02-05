package tn.enis.pfa.dto;

import lombok.Data;
import tn.enis.pfa.entity.Course;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CourseDto {

    private Long id;
    private String title;
    private String description;
    private String category;
    private Long teacherId;
    private String teacherName;
    private Instant createdAt;
    private List<ModuleDto> modules;
    private Integer enrollmentCount;

    public static CourseDto from(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCategory(course.getCategory());
        dto.setTeacherId(course.getTeacher().getId());
        dto.setTeacherName(course.getTeacher().getFullName());
        dto.setCreatedAt(course.getCreatedAt());
        if (course.getModules() != null) {
            dto.setModules(course.getModules().stream().map(ModuleDto::from).collect(Collectors.toList()));
        }
        if (course.getEnrollments() != null) {
            dto.setEnrollmentCount(course.getEnrollments().size());
        }
        return dto;
    }

    public static CourseDto fromSummary(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCategory(course.getCategory());
        dto.setTeacherId(course.getTeacher().getId());
        dto.setTeacherName(course.getTeacher().getFullName());
        dto.setCreatedAt(course.getCreatedAt());
        if (course.getEnrollments() != null) {
            dto.setEnrollmentCount(course.getEnrollments().size());
        }
        return dto;
    }
}
