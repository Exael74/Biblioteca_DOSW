package edu.eci.dosw.tdd.core.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;

    @NotBlank(message = "El nombre del usuario es obligatorio")
    private String name;

    @NotBlank(message = "El username es obligatorio")
    private String username;

    private String password;

    private Role role;

    // Campos extendidos para MongoDB
    private String email;
    private String membershipType; // VIP, PLATINUM, STANDARD
    private LocalDate registrationDate;
}