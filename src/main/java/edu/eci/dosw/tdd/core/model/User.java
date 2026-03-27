package edu.eci.dosw.tdd.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String username;
    private String password;

    @Builder.Default
    private Role role = Role.USER;
    
    @Builder.Default
    private List<Loan> loans = new ArrayList<>();

    public enum Role {
        LIBRARIAN,
        USER
    }
}
