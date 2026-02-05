package tn.enis.pfa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExerciseCreateRequest {

    @NotBlank
    private String question;

    @NotBlank
    private String type;

    private String correctAnswer;
    private String optionsJson;

    @NotNull
    private Integer orderIndex;
}
