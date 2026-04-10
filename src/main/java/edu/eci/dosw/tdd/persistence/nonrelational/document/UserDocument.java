package edu.eci.dosw.tdd.persistence.nonrelational.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserDocument {

    @Id
    private String id;

    private String name;

    private String username;

    private String password;

    private String role; // LIBRARIAN, USER

    private String email;

    private String membershipType; // BASIC, PREMIUM

    private LocalDate registrationDate;
}
