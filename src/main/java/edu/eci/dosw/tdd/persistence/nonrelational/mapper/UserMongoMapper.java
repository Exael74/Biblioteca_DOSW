package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import org.springframework.stereotype.Component;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.nonrelational.document.UserDocument;

@Component
public class UserMongoMapper {

    public User toDomain(UserDocument document) {
        if (document == null) {
            return null;
        }
        return new User(
            document.getId(),
            document.getName(),
            document.getUsername(),
            document.getPassword(),
            User.Role.valueOf(document.getRole()),
            new java.util.ArrayList<>()
        );
    }

    public UserDocument toDocument(User domain) {
        if (domain == null) {
            return null;
        }
        return UserDocument.builder()
            .id(domain.getId())
            .name(domain.getName())
            .username(domain.getUsername())
            .password(domain.getPassword())
            .role(domain.getRole().name())
            .build();
    }
}
