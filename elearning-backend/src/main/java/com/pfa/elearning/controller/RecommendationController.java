package com.pfa.elearning.controller;

import com.pfa.elearning.dto.response.RecommendationResponse;
import com.pfa.elearning.model.User;
import com.pfa.elearning.service.RecommendationService;
import com.pfa.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<RecommendationResponse>> getMyRecommendations(
            Authentication authentication) {
        User student = userService.getUserByEmail(authentication.getName());
        List<RecommendationResponse> recommendations =
                recommendationService.getStudentRecommendations(student.getId());
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/{id}/click")
    public ResponseEntity<Void> markAsClicked(@PathVariable Long id) {
        recommendationService.markAsClicked(id);
        return ResponseEntity.ok().build();
    }
}
