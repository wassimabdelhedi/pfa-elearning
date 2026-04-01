package com.pfa.elearning.controller;

import com.pfa.elearning.dto.request.SearchRequest;
import com.pfa.elearning.dto.response.SearchResponse;
import com.pfa.elearning.model.SearchHistory;
import com.pfa.elearning.model.User;
import com.pfa.elearning.repository.SearchHistoryRepository;
import com.pfa.elearning.service.SearchService;
import com.pfa.elearning.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final UserService userService;
    private final SearchHistoryRepository searchHistoryRepository;

    @PostMapping
    public ResponseEntity<SearchResponse> search(
            @Valid @RequestBody SearchRequest request,
            Authentication authentication) {
        User student = userService.getUserByEmail(authentication.getName());
        SearchResponse response = searchService.search(request.getQuery(), student);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getSearchHistory(Authentication authentication) {
        User student = userService.getUserByEmail(authentication.getName());
        List<SearchHistory> history =
                searchHistoryRepository.findTop10ByStudentIdOrderBySearchedAtDesc(student.getId());

        List<Map<String, Object>> result = history.stream().map(h -> Map.<String, Object>of(
                "id", h.getId(),
                "query", h.getQuery(),
                "extractedKeywords", h.getExtractedKeywords() != null ? h.getExtractedKeywords() : "",
                "resultsCount", h.getResultsCount(),
                "searchedAt", h.getSearchedAt().toString()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
