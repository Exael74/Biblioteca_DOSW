package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoginRequest;
import edu.eci.dosw.tdd.controller.dto.LoginResponse;
import edu.eci.dosw.tdd.controller.dto.RegisterRequest;
import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(
                request.getName(),
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getMembershipType()
        );
        return new ResponseEntity<>(userMapper.toDTO(user), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping("/register-librarian")
    public ResponseEntity<UserDTO> registerLibrarian(@Valid @RequestBody RegisterRequest request) {
        User user = authService.registerWithRole(
                request.getName(),
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getMembershipType(),
                Role.LIBRARIAN
        );
        return new ResponseEntity<>(userMapper.toDTO(user), HttpStatus.CREATED);
    }
}