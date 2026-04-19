package com.pfa.elearning.controller;

import com.pfa.elearning.model.Message;
import com.pfa.elearning.model.User;
import com.pfa.elearning.service.MessageService;
import com.pfa.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        
        User sender = userService.getUserByEmail(authentication.getName());
        Long receiverId = ((Number) body.get("receiverId")).longValue();
        String content = (String) body.get("content");
        
        Message message = messageService.sendMessage(sender.getId(), receiverId, content);
        return ResponseEntity.ok(toDto(message));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<Map<String, Object>>> getConversations(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        List<Map<String, Object>> previews = messageService.getConversationsPreview(user.getId());
        return ResponseEntity.ok(previews);
    }

    @GetMapping("/history/{contactId}")
    public ResponseEntity<List<Map<String, Object>>> getConversationHistory(
            @PathVariable Long contactId,
            Authentication authentication) {
        
        User user = userService.getUserByEmail(authentication.getName());
        List<Message> history = messageService.getConversation(user.getId(), contactId);
        
        List<Map<String, Object>> dtos = history.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/debug/users")
    public ResponseEntity<List<Map<String, Object>>> debugUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getFullName());
            map.put("email", u.getEmail());
            map.put("role", u.getRole() != null ? u.getRole().name() : "null");
            return map;
        }).collect(Collectors.toList()));
    }

    // Helper fetch to get users you can chat with (Teachers vs Students)
    @GetMapping("/contacts/search")
    public ResponseEntity<List<Map<String, Object>>> searchContacts(
            @RequestParam(required = false, defaultValue = "") String query,
            Authentication authentication) {
        
        User currentUser = userService.getUserByEmail(authentication.getName());
        
        List<User> allUsers = userService.getAllUsers();
        List<String> keywords = Arrays.asList(query.toLowerCase().trim().split("\\s+"));
        
        List<Map<String, Object>> dtos = allUsers.stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .filter(u -> {
                    if (query.trim().isEmpty()) return true;
                    // Every keyword must match either name, email or role
                    return keywords.stream().allMatch(keyword -> {
                        String name = u.getFullName() != null ? u.getFullName().toLowerCase() : "";
                        String email = u.getEmail() != null ? u.getEmail().toLowerCase() : "";
                        String role = u.getRole() != null ? u.getRole().name() : "";

                        boolean matchesName = name.contains(keyword);
                        boolean matchesEmail = email.contains(keyword);
                        boolean isTeacher = role.equals("TEACHER");
                        boolean isStudent = role.equals("STUDENT");
                        boolean matchesRole = (isTeacher && (keyword.equals("enseignant") || keyword.equals("prof") || keyword.equals("teacher"))) ||
                                              (isStudent && (keyword.equals("etudiant") || keyword.equals("étudiant") || keyword.equals("student")));
                        
                        return matchesName || matchesEmail || matchesRole;
                    });
                })
                // Removed the restrictive role filters so anyone can search anyone
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("name", u.getFullName());
                    map.put("email", u.getEmail());
                    map.put("role", u.getRole().name());
                    return map;
                })
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

    private Map<String, Object> toDto(Message m) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", m.getId());
        dto.put("senderId", m.getSender().getId());
        dto.put("senderName", m.getSender().getFullName());
        dto.put("receiverId", m.getReceiver().getId());
        dto.put("receiverName", m.getReceiver().getFullName());
        dto.put("content", m.getContent());
        dto.put("sentAt", m.getSentAt());
        dto.put("read", m.isRead());
        return dto;
    }
}
