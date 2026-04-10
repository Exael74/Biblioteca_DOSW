package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Base64 encoded key (at least 256 bits for HMAC-SHA)
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "ZG9zdy1saWJyYXJ5LXNlY3JldC1rZXktMjAyNi1zdXBlci1zZWN1cmUtand0LXNpZ25pbmcta2V5LTEyMzQ1Njc4OTA=");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    @Test
    @DisplayName("Generar token retorna string no vacío")
    void generateToken_returnsNonEmptyString() {
        String token = jwtService.generateToken("user-001", "juan", Role.USER);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    @DisplayName("Extraer username del token")
    void extractUsername_returnsCorrectUsername() {
        String token = jwtService.generateToken("user-001", "juan", Role.USER);

        String username = jwtService.extractUsername(token);

        assertEquals("juan", username);
    }

    @Test
    @DisplayName("Extraer userId del token")
    void extractUserId_returnsCorrectId() {
        String token = jwtService.generateToken("user-001", "juan", Role.USER);

        String userId = jwtService.extractUserId(token);

        assertEquals("user-001", userId);
    }

    @Test
    @DisplayName("Extraer rol del token")
    void extractRole_returnsCorrectRole() {
        String token = jwtService.generateToken("user-001", "juan", Role.LIBRARIAN);

        String role = jwtService.extractRole(token);

        assertEquals("LIBRARIAN", role);
    }

    @Test
    @DisplayName("Token válido retorna true")
    void isTokenValid_validToken_returnsTrue() {
        String token = jwtService.generateToken("user-001", "juan", Role.USER);

        assertTrue(jwtService.isTokenValid(token, "juan"));
    }

    @Test
    @DisplayName("Token con username incorrecto retorna false")
    void isTokenValid_wrongUsername_returnsFalse() {
        String token = jwtService.generateToken("user-001", "juan", Role.USER);

        assertFalse(jwtService.isTokenValid(token, "pedro"));
    }

    @Test
    @DisplayName("Token expirado lanza excepción")
    void isTokenValid_expiredToken_throwsException() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);

        String token = jwtService.generateToken("user-001", "juan", Role.USER);

        assertThrows(Exception.class, () -> jwtService.isTokenValid(token, "juan"));
    }

    @Test
    @DisplayName("Token inválido lanza excepción")
    void extractUsername_invalidToken_throwsException() {
        assertThrows(Exception.class, () -> jwtService.extractUsername("invalid-token"));
    }

    @Test
    @DisplayName("Generar token con rol USER")
    void generateToken_userRole() {
        String token = jwtService.generateToken("user-002", "maria", Role.USER);
        assertEquals("USER", jwtService.extractRole(token));
        assertEquals("maria", jwtService.extractUsername(token));
        assertEquals("user-002", jwtService.extractUserId(token));
    }
}
