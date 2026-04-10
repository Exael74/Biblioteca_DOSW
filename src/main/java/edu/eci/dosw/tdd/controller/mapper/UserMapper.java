package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setEmail(user.getEmail());
        dto.setMembershipType(user.getMembershipType());
        dto.setRegistrationDate(user.getRegistrationDate());
        return dto;
    }

    public User toDomain(UserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        if (dto.getRole() != null) {
            user.setRole(Role.valueOf(dto.getRole()));
        }
        user.setEmail(dto.getEmail());
        user.setMembershipType(dto.getMembershipType());
        user.setRegistrationDate(dto.getRegistrationDate());
        return user;
    }
}
