package tn.enis.pfa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enis.pfa.dto.ProgressDto;
import tn.enis.pfa.dto.ProgressRequest;
import tn.enis.pfa.security.CurrentUserId;
import tn.enis.pfa.service.ProgressService;

import java.util.List;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<List<ProgressDto>> byEnrollment(@CurrentUserId Long userId,
                                                          @PathVariable Long enrollmentId) {
        return ResponseEntity.ok(progressService.findByEnrollment(enrollmentId));
    }

    @PostMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<ProgressDto> record(@CurrentUserId Long userId,
                                              @PathVariable Long enrollmentId,
                                              @RequestBody ProgressRequest request) {
        return ResponseEntity.ok(progressService.recordProgress(userId, enrollmentId, request));
    }
}
