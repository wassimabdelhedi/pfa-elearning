package tn.enis.pfa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enis.pfa.dto.UserDto;
import tn.enis.pfa.entity.User;
import tn.enis.pfa.repository.UserRepository;
import tn.enis.pfa.security.CurrentUserId;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@CurrentUserId Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        return ResponseEntity.ok(UserDto.from(user));
    }
}
