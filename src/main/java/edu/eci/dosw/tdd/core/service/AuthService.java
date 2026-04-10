package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.controller.dto.LoginResponse;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.ApiMessages;
import edu.eci.dosw.tdd.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(ApiMessages.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(ApiMessages.INVALID_CREDENTIALS);
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }

    public User register(String name, String username, String password, String email, String membershipType) {
        return registerWithRole(name, username, password, email, membershipType, Role.USER);
    }

    public User registerWithRole(String name, String username, String password,
                                  String email, String membershipType, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(ApiMessages.USERNAME_ALREADY_EXISTS);
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(name);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEmail(email);
        user.setMembershipType(membershipType != null ? membershipType : "STANDARD");
        user.setRegistrationDate(LocalDate.now());

        return userRepository.save(user);
    }
}