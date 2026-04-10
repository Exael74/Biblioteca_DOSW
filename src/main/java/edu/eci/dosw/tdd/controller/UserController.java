package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.CreateUserRequest;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Secured("ROLE_LIBRARIAN")
    @PostMapping
    public User createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.addUser(User.builder()
                .name(request.name())
                .username(request.username())
                .password(request.password())
                .role(request.role())
                .build());
    }

    @Secured("ROLE_LIBRARIAN")
    @GetMapping
    public List<User> listUsers() {
        return userService.getAllUsers();
    }
}