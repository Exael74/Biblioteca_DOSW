package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.nonrelational.document.UserDocument;
import org.springframework.stereotype.Component;

@Component
public class UserDocumentMapper {

    public User toDomain(UserDocument doc) {
        if (doc == null) return null;

        User user = new User();
        user.setId(doc.getId());
        user.setName(doc.getName());
        user.setUsername(doc.getUsername());
        user.setPassword(doc.getPassword());
        if (doc.getRole() != null) {
            user.setRole(Role.valueOf(doc.getRole()));
        }
        user.setEmail(doc.getEmail());
        user.setMembershipType(doc.getMembershipType());
        user.setRegistrationDate(doc.getRegistrationDate());

        return user;
    }

    public UserDocument toDocument(User user) {
        if (user == null) return null;

        UserDocument doc = new UserDocument();
        doc.setId(user.getId());
        doc.setName(user.getName());
        doc.setUsername(user.getUsername());
        doc.setPassword(user.getPassword());
        if (user.getRole() != null) {
            doc.setRole(user.getRole().name());
        }
        doc.setEmail(user.getEmail());
        doc.setMembershipType(user.getMembershipType());
        doc.setRegistrationDate(user.getRegistrationDate());

        return doc;
    }
}
