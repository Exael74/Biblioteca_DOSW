package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.controller.dto.LoginResponse;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-001");
        testUser.setName("Juan");
        testUser.setUsername("juan");
        testUser.setPassword("$2a$10$hashedpassword");
        testUser.setRole(Role.USER);
    }

    // --- login ---

    @Test
    @DisplayName("Login exitoso retorna token")
    void login_success() {
        when(userRepository.findByUsername("juan")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedpassword")).thenReturn(true);
        when(jwtService.generateToken("user-001", "juan", Role.USER)).thenReturn("jwt-token");

        LoginResponse response = authService.login("juan", "password123");

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("juan", response.getUsername());
        assertEquals("USER", response.getRole());
    }

    @Test
    @DisplayName("Login con usuario inexistente lanza excepción")
    void login_userNotFound_throwsException() {
        when(userRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.login("noexiste", "password"));
    }

    @Test
    @DisplayName("Login con contraseña incorrecta lanza excepción")
    void login_wrongPassword_throwsException() {
        when(userRepository.findByUsername("juan")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpass", "$2a$10$hashedpassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login("juan", "wrongpass"));
    }

    // --- register ---

    @Test
    @DisplayName("Registro exitoso como USER")
    void register_success() {
        when(userRepository.existsByUsername("nuevo")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.register("Nuevo User", "nuevo", "pass123", "nuevo@mail.com", "STANDARD");

        assertNotNull(result);
        assertEquals("Nuevo User", result.getName());
        assertEquals("nuevo", result.getUsername());
        assertEquals(Role.USER, result.getRole());
        assertEquals("nuevo@mail.com", result.getEmail());
        assertEquals("STANDARD", result.getMembershipType());
        assertNotNull(result.getRegistrationDate());
    }

    @Test
    @DisplayName("Registro con username duplicado lanza excepción")
    void register_duplicateUsername_throwsException() {
        when(userRepository.existsByUsername("juan")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> authService.register("Juan", "juan", "pass", "j@mail.com", null));
    }

    @Test
    @DisplayName("Registro sin membershipType asigna STANDARD")
    void register_nullMembership_defaultsToStandard() {
        when(userRepository.existsByUsername("test")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.register("Test", "test", "pass", null, null);

        assertEquals("STANDARD", result.getMembershipType());
    }

    // --- registerWithRole ---

    @Test
    @DisplayName("Registro como LIBRARIAN exitoso")
    void registerWithRole_librarian_success() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("adminpass")).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.registerWithRole("Admin", "admin", "adminpass", "admin@mail.com", "VIP", Role.LIBRARIAN);

        assertNotNull(result);
        assertEquals(Role.LIBRARIAN, result.getRole());
        assertEquals("admin", result.getUsername());
    }

    @Test
    @DisplayName("Registro con rol duplicado username lanza excepción")
    void registerWithRole_duplicateUsername_throwsException() {
        when(userRepository.existsByUsername("juan")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> authService.registerWithRole("Juan", "juan", "pass", null, null, Role.LIBRARIAN));
    }
}
