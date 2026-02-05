package tn.enis.pfa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourseCreateRequest {

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String category;
}
