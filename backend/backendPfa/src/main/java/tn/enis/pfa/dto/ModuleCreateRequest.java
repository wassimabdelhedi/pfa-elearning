package tn.enis.pfa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModuleCreateRequest {

    @NotBlank
    private String title;

    @NotNull
    private Integer orderIndex;
}
