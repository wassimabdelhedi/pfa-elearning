package tn.enis.pfa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enis.pfa.dto.RecommendationDto;
import tn.enis.pfa.security.CurrentUserId;
import tn.enis.pfa.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/me")
    public ResponseEntity<List<RecommendationDto>> myRecommendations(@CurrentUserId Long userId) {
        return ResponseEntity.ok(recommendationService.getForUser(userId));
    }
}
