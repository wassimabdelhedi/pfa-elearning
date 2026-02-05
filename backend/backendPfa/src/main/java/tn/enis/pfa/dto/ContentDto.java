package tn.enis.pfa.dto;

import lombok.Data;
import tn.enis.pfa.entity.Content;

@Data
public class ContentDto {

    private Long id;
    private String title;
    private String type;
    private String body;
    private String videoUrl;
    private Integer orderIndex;

    public static ContentDto from(Content content) {
        ContentDto dto = new ContentDto();
        dto.setId(content.getId());
        dto.setTitle(content.getTitle());
        dto.setType(content.getType().name());
        dto.setBody(content.getBody());
        dto.setVideoUrl(content.getVideoUrl());
        dto.setOrderIndex(content.getOrderIndex());
        return dto;
    }
}
