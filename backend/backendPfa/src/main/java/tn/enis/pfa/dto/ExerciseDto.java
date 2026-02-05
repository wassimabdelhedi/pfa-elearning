package tn.enis.pfa.dto;

import lombok.Data;
import tn.enis.pfa.entity.Exercise;

@Data
public class ExerciseDto {

    private Long id;
    private String question;
    private String type;
    private String correctAnswer;
    private String optionsJson;
    private Integer orderIndex;

    public static ExerciseDto from(Exercise exercise) {
        ExerciseDto dto = new ExerciseDto();
        dto.setId(exercise.getId());
        dto.setQuestion(exercise.getQuestion());
        dto.setType(exercise.getType().name());
        dto.setCorrectAnswer(exercise.getCorrectAnswer());
        dto.setOptionsJson(exercise.getOptionsJson());
        dto.setOrderIndex(exercise.getOrderIndex());
        return dto;
    }
}
