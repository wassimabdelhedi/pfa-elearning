package tn.enis.pfa.dto;

import lombok.Data;
import tn.enis.pfa.entity.CourseModule;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ModuleDto {

    private Long id;
    private String title;
    private Integer orderIndex;
    private List<ContentDto> contents;
    private List<ExerciseDto> exercises;

    public static ModuleDto from(CourseModule module) {
        ModuleDto dto = new ModuleDto();
        dto.setId(module.getId());
        dto.setTitle(module.getTitle());
        dto.setOrderIndex(module.getOrderIndex());
        if (module.getContents() != null) {
            dto.setContents(module.getContents().stream().map(ContentDto::from).collect(Collectors.toList()));
        }
        if (module.getExercises() != null) {
            dto.setExercises(module.getExercises().stream().map(ExerciseDto::from).collect(Collectors.toList()));
        }
        return dto;
    }
}
