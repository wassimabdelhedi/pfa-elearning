package tn.enis.pfa.dto;

import lombok.Data;
import tn.enis.pfa.entity.User;

import java.time.Instant;

@Data
public class UserDto {

    private Long id;
    private String email;
    private String fullName;
    private String role;
    private Instant createdAt;

    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
