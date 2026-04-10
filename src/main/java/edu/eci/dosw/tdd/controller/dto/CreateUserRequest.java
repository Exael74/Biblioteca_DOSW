package edu.eci.dosw.tdd.controller.dto;

import edu.eci.dosw.tdd.core.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Password is required") String password,
        @NotNull(message = "Role is required") User.Role role
) {
}