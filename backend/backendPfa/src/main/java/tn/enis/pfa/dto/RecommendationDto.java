package tn.enis.pfa.dto;

import lombok.Data;
import tn.enis.pfa.entity.Recommendation;

import java.time.Instant;

@Data
public class RecommendationDto {

    private Long id;
    private String type;
    private Long courseId;
    private String courseTitle;
    private Long contentId;
    private Long moduleId;
    private String reason;
    private Double score;
    private Instant createdAt;

    public static RecommendationDto from(Recommendation r) {
        RecommendationDto dto = new RecommendationDto();
        dto.setId(r.getId());
        dto.setType(r.getType().name());
        dto.setCourseId(r.getCourseId());
        dto.setContentId(r.getContentId());
        dto.setModuleId(r.getModuleId());
        dto.setReason(r.getReason());
        dto.setScore(r.getScore());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
