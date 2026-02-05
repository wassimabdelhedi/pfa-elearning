package tn.enis.pfa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContentCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String type;

    private String body;
    private String videoUrl;

    @NotNull
    private Integer orderIndex;
}
