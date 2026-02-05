package tn.enis.pfa.dto;

import lombok.Data;

@Data
public class ProgressRequest {

    private Long contentId;
    private Long exerciseId;
    private Double score;
    private Boolean completed;
}
